package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.SFM;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collection;

@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(value = false)
public class SFMGameTestDiscovery {
    @GameTestGenerator
    public Collection<TestFunction> generateTests() {
        ArrayList<TestFunction> rtn = new ArrayList<>();
        Type sfm_test_annotation = Type.getType(SFMGameTest.class);
        ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> sfm_test_annotation.equals(a.annotationType()))
                .map(a -> {
                    // load the class
                    try {
                        return Class.forName(a.clazz().getClassName(), true, SFMGameTestDiscovery.class.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(clazz -> {
                    // construct test definition
                    if (!SFMGameTestDefinition.class.isAssignableFrom(clazz)) {
                        throw new RuntimeException("Class " + clazz.getName() + " does not extend SFMGameTestDefinition");
                    }
                    try {
                        SFMGameTestDefinition sfmGameTestDefinition = (SFMGameTestDefinition) clazz
                                .getConstructor()
                                .newInstance();
                        SFM.LOGGER.info("Discovered SFM game test: {}", sfmGameTestDefinition.testName());
                        return sfmGameTestDefinition;
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("Failed to instantiate test builder for " + clazz.getName(), e);
                    }
                })
                .forEach(testDefinition -> rtn.add(testDefinition.intoTestFunction()));

        return rtn;
    }
}

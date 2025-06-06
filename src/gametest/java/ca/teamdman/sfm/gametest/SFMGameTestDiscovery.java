package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.SFM;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforgespi.language.ModFileScanData;
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
                        return (SFMGameTestDefinition) clazz.getConstructor().newInstance();
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("Failed to instantiate test builder for " + clazz.getName(), e);
                    }
                })
                .forEach(testDefinition -> rtn.add(testDefinition.intoTestFunction()));

        return rtn;
    }
}

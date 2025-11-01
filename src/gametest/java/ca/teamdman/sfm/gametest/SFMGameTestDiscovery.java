package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.SFM;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@GameTestHolder(SFM.MOD_ID)
//@PrefixGameTestTemplate(value = false)
@EventBusSubscriber
public class SFMGameTestDiscovery {
    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        // Discover our tests
        Collection<SFMGameTestDefinition> tests = SFMGameTestDiscovery.gatherTests().toList();

        // Manually register the tests
        Collection<TestFunction> allTestFunctions = GameTestRegistry.getAllTestFunctions();
        Collection<String> allTestClassNames = GameTestRegistry.getAllTestClassNames();

        for (SFMGameTestDefinition test : tests) {
            allTestFunctions.add(test.intoTestFunction());
            allTestClassNames.add(test.testName());
        }
    }

    public static Stream<SFMGameTestDefinition> gatherTests() {
        Type sfm_test_annotation = Type.getType(SFMGameTest.class);
        return ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> sfm_test_annotation.equals(a.annotationType()))
                .map(a -> {
                    // load the class
                    try {
                        return Class.forName(
                                a.clazz().getClassName(),
                                true,
                                SFMGameTestDiscovery.class.getClassLoader()
                        );
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(clazz -> {
                    // construct test definition
                    if (!SFMGameTestDefinition.class.isAssignableFrom(clazz)) {
                        throw new RuntimeException("Class "
                                                   + clazz.getName()
                                                   + " does not extend SFMGameTestDefinition");
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
                });
    }

    //    @GameTestGenerator
    public Collection<TestFunction> generateTests() {
        return gatherTests()
                .map(SFMGameTestDefinition::intoTestFunction)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

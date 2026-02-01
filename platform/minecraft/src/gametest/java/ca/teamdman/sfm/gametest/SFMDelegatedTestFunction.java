package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;

/**
 * This class was used in MC < 1.21.0 to wrap {@link SFMGameTestDefinition} instances
 * as {@link net.minecraft.gametest.framework.TestFunction} objects by extending TestFunction.
 * <p>
 * In MC >= 1.21.0, {@link net.minecraft.gametest.framework.TestFunction} became a record
 * and can no longer be extended. {@link SFMGameTestDefinition#intoTestFunction()} now
 * directly constructs the TestFunction instead.
 * <p>
 * This stub class is kept for propagation compatibility between version branches.
 *
 * @see SFMGameTestDefinition#intoTestFunction()
 */
@MCVersionDependentBehaviour
public class SFMDelegatedTestFunction {
    // No longer needed in MC >= 1.21.0 - TestFunction is now a record and cannot be extended
    // See SFMGameTestDefinition#intoTestFunction() for the new approach
}

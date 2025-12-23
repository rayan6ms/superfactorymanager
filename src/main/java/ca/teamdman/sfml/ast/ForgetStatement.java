package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ExecuteProgramBehaviour;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.SimulateExploreAllPathsProgramBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ca.teamdman.sfm.common.localization.LocalizationKeys.LOG_PROGRAM_TICK_FORGET_STATEMENT;

public record ForgetStatement(
        Set<Label> labelToForget
) implements Tickable {
    @Override
    public void tick(ProgramContext context) {
        List<InputStatement> newInputs = new ArrayList<>();

        for (InputStatement oldInputStatement : context.inputs()) {
            var newLabels = oldInputStatement.resourceAccess().labelExpressions().stream()
                    .filter(label -> !this.labelToForget.contains(label))
                    .toList();

            // always fire event from old to new, even if new has no labels
            InputStatement newInputStatement = new InputStatement(
                    new ResourceAccess(
                            newLabels,
                            oldInputStatement.resourceAccess().roundRobin(), oldInputStatement.resourceAccess().sides(),
                            oldInputStatement.resourceAccess().slots()
                    ),
                    oldInputStatement.resourceLimits(),
                    oldInputStatement.each()
            );

            if (!(context.behaviour() instanceof ExecuteProgramBehaviour)) {
                /// This is a waste when running the program.
                /// Only needed for {@link ca.teamdman.sfm.common.program.linting.GatherWarningsProgramBehaviour}.
                /// Will allow it for other non-execute behaviours just to avoid trouble.

                context.program().astBuilder().setLocationFromOtherNode(newInputStatement, oldInputStatement);
            }

            if (context.behaviour() instanceof SimulateExploreAllPathsProgramBehaviour simulation) {
                simulation.onInputStatementForgetTransform(context, oldInputStatement, newInputStatement);
            }
            // this could be a set instead of list contains check, but whatever. Should be small
            oldInputStatement.freeSlotsIf(slot -> labelToForget.contains(slot.label));
            oldInputStatement.transferSlotsTo(newInputStatement);

            if (newLabels.isEmpty()) {
                oldInputStatement.freeSlots();
            } else {
                newInputs.add(newInputStatement);
            }
        }

        context.inputs().clear();

        context.inputs().addAll(newInputs);

        context.logger().debug(x -> x.accept(LOG_PROGRAM_TICK_FORGET_STATEMENT.get(
                labelToForget.stream().map(Objects::toString).collect(Collectors.joining(", "))
        )));
    }

    @Override
    public String toString() {
        return "FORGET " + labelToForget.stream().map(Objects::toString).collect(Collectors.joining(", "));
    }

    @Override
    public List<ASTNode> getChildNodes() {

        return List.of();
    }

}

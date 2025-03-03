package ca.teamdman.sfm.common.util;

import ca.teamdman.sfml.ast.ASTNode;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.intellisense.IntellisenseAction;
import ca.teamdman.sfml.intellisense.IntellisenseContext;
import ca.teamdman.sfml.intellisense.SFMLIntellisense;
import com.mojang.datafixers.util.Pair;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class SFMDisplayUtils {
    public static String getCursorDisplay(
            String programString,
            int cursorPos
    ) {
        StringBuilder rtn = new StringBuilder();
        rtn.append(" [");
        // print the 10-closest characters before the cursor
        int start = Math.max(0, cursorPos - 10);
        int end = cursorPos;
        rtn.append(String.format("%20s", programString.substring(start, end).replaceAll("\n", "\\\\n")));

        // print |
        rtn.append("|");

        // print the 10 characters after the cursor
        start = cursorPos;
        end = Math.min(programString.length(), cursorPos + 10);
        rtn.append(String.format("%-20s", programString.substring(start, end).replaceAll("\n", "\\\\n")));
        rtn.append(" ] ");
        return rtn.toString();
    }

    public static String getTokenHierarchyDisplay(
            Program program,
            int cursorPos
    ) {
        StringBuilder rtn = new StringBuilder();
        List<Pair<ASTNode, ParserRuleContext>> nodesUnderCursor = program.builder().getNodesUnderCursor(cursorPos);

        var iter = nodesUnderCursor.listIterator(nodesUnderCursor.size());
        while (iter.hasPrevious()) {
            Pair<ASTNode, ParserRuleContext> pair = iter.previous();
            ASTNode node = pair.getFirst();
            rtn.append(node.getClass().getSimpleName());
            if (iter.hasPrevious()) {
                rtn.append(" -> ");
            }
        }
        return rtn.toString();
    }

    public static String getSuggestionsDisplay(
            String programString,
            int cursorPos
    ) {
        StringBuilder rtn = new StringBuilder();
        List<IntellisenseAction> suggestions = SFMLIntellisense.getSuggestions(new IntellisenseContext(
                programString,
                cursorPos,
                0
        ));
        rtn.append('[');
        for (int i = 0; i < suggestions.size(); i++) {
            rtn.append(suggestions.get(i).getDisplayText().replaceAll("\n", "\\\\n"));
            if (i != suggestions.size() - 1) {
                rtn.append(", ");
            }
        }
        rtn.append(']');
        return rtn.toString();
    }
}

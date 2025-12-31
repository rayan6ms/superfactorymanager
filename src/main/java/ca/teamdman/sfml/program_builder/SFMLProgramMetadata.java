package ca.teamdman.sfml.program_builder;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.langs.SFMLParser;
import ca.teamdman.sfml.ast.SfmlAstBuilder;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

public record SFMLProgramMetadata(
        String programString,
        SFMLLexer lexer,
        CommonTokenStream tokens,
        SFMLParser parser,
        SfmlAstBuilder astBuilder,
        List<TranslatableContents> errors
) implements IProgramMetadata<SFMLLexer, SFMLParser, SfmlAstBuilder> {
}

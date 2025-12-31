package ca.teamdman.sfml.program_builder;

import net.minecraft.network.chat.contents.TranslatableContents;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

import java.util.List;

public interface IProgramMetadata<LEXER extends Lexer, PARSER extends Parser, BUILDER> {
    String programString();

    LEXER lexer();

    CommonTokenStream tokens();

    PARSER parser();

    BUILDER astBuilder();

    List<TranslatableContents> errors();

}

package ca.teamdman.ai;

import ca.teamdman.sfm.common.ai.SFMAIModelList;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ListLanguageModelsTest {
    @Test
    public void listLocalModels() throws IOException {
        String models = new SFMAIModelList().fetchModels();
        System.out.println(models);
    }
}

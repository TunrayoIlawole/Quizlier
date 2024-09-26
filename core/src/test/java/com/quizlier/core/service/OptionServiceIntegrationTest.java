package com.quizlier.core.service;

import com.quizlier.common.dto.OptionRequest;
import com.quizlier.common.dto.OptionResponse;
import com.quizlier.common.entity.Option;
import com.quizlier.core.AbstractIntegrationTest;
import com.quizlier.core.TestUtil;
import com.quizlier.core.repository.OptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OptionServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private OptionService optionService;

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void createOption_Success() {
        OptionRequest optionRequest = TestUtil.buildOptionRequest();

        OptionResponse optionResponse = optionService.createOption(optionRequest, 1L);

        assertNotNull(optionResponse);

        Optional<Option> savedOption = optionRepository.findById(optionResponse.getId());
        assertTrue(savedOption.isPresent());

    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void getOptions_Success() {

        OptionRequest optionRequest = TestUtil.buildCorrectOptionRequest();

        OptionResponse optionResponse = optionService.createOption(optionRequest, 1L);

        List<OptionResponse> optionResponses = optionService.getAllOptionsByQuestions(optionResponse.getQuestionId());

        assertNotNull(optionResponses);
        assertEquals(4, optionResponses.size());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void updateOption_Success() {
        OptionRequest optionRequest = TestUtil.buildCorrectOptionRequest();

        OptionResponse optionResponse = optionService.createOption(optionRequest, 1L);

        OptionRequest updateOptionRequest = new OptionRequest();
        updateOptionRequest.setOptionText(optionRequest.getOptionText());
        updateOptionRequest.setIsCorrect(false);

        OptionResponse updateOptionResponse = optionService.updateOption(optionResponse.getId(), updateOptionRequest);

        assertNotNull(updateOptionResponse);

        Optional<Option> updatedOption = optionRepository.findById(optionResponse.getId());
        assertTrue(updatedOption.isPresent());
        assertFalse(updatedOption.get().getIsCorrect());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_script.sql", "classpath:/sql/insert_entities.sql"})
    void deleteOption_Success() {
        OptionRequest optionRequest = TestUtil.buildCorrectOptionRequest();

        OptionResponse optionResponse = optionService.createOption(optionRequest, 1L);

        optionService.deleteOption(optionResponse.getId());

        Optional<Option> deletedOption = optionRepository.findById(optionResponse.getId());
        assertFalse(deletedOption.isPresent());
    }

}

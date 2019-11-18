package com.sidbisht.jhip.defecttracker.web.rest;

import com.sidbisht.jhip.defecttracker.JhipDefectTrackerApp;
import com.sidbisht.jhip.defecttracker.domain.Label;
import com.sidbisht.jhip.defecttracker.repository.LabelRepository;
import com.sidbisht.jhip.defecttracker.service.LabelService;
import com.sidbisht.jhip.defecttracker.web.rest.errors.ExceptionTranslator;
import com.sidbisht.jhip.defecttracker.service.dto.LabelCriteria;
import com.sidbisht.jhip.defecttracker.service.LabelQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.sidbisht.jhip.defecttracker.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link LabelResource} REST controller.
 */
@SpringBootTest(classes = JhipDefectTrackerApp.class)
public class LabelResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelService labelService;

    @Autowired
    private LabelQueryService labelQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restLabelMockMvc;

    private Label label;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LabelResource labelResource = new LabelResource(labelService, labelQueryService);
        this.restLabelMockMvc = MockMvcBuilders.standaloneSetup(labelResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Label createEntity(EntityManager em) {
        Label label = new Label()
            .label(DEFAULT_LABEL);
        return label;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Label createUpdatedEntity(EntityManager em) {
        Label label = new Label()
            .label(UPDATED_LABEL);
        return label;
    }

    @BeforeEach
    public void initTest() {
        label = createEntity(em);
    }

    @Test
    @Transactional
    public void createLabel() throws Exception {
        int databaseSizeBeforeCreate = labelRepository.findAll().size();

        // Create the Label
        restLabelMockMvc.perform(post("/api/labels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(label)))
            .andExpect(status().isCreated());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeCreate + 1);
        Label testLabel = labelList.get(labelList.size() - 1);
        assertThat(testLabel.getLabel()).isEqualTo(DEFAULT_LABEL);
    }

    @Test
    @Transactional
    public void createLabelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = labelRepository.findAll().size();

        // Create the Label with an existing ID
        label.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLabelMockMvc.perform(post("/api/labels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(label)))
            .andExpect(status().isBadRequest());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = labelRepository.findAll().size();
        // set the field null
        label.setLabel(null);

        // Create the Label, which fails.

        restLabelMockMvc.perform(post("/api/labels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(label)))
            .andExpect(status().isBadRequest());

        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLabels() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList
        restLabelMockMvc.perform(get("/api/labels?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(label.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)));
    }
    
    @Test
    @Transactional
    public void getLabel() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get the label
        restLabelMockMvc.perform(get("/api/labels/{id}", label.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(label.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL));
    }


    @Test
    @Transactional
    public void getLabelsByIdFiltering() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        Long id = label.getId();

        defaultLabelShouldBeFound("id.equals=" + id);
        defaultLabelShouldNotBeFound("id.notEquals=" + id);

        defaultLabelShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLabelShouldNotBeFound("id.greaterThan=" + id);

        defaultLabelShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLabelShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllLabelsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where label equals to DEFAULT_LABEL
        defaultLabelShouldBeFound("label.equals=" + DEFAULT_LABEL);

        // Get all the labelList where label equals to UPDATED_LABEL
        defaultLabelShouldNotBeFound("label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void getAllLabelsByLabelIsNotEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where label not equals to DEFAULT_LABEL
        defaultLabelShouldNotBeFound("label.notEquals=" + DEFAULT_LABEL);

        // Get all the labelList where label not equals to UPDATED_LABEL
        defaultLabelShouldBeFound("label.notEquals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void getAllLabelsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where label in DEFAULT_LABEL or UPDATED_LABEL
        defaultLabelShouldBeFound("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL);

        // Get all the labelList where label equals to UPDATED_LABEL
        defaultLabelShouldNotBeFound("label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void getAllLabelsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where label is not null
        defaultLabelShouldBeFound("label.specified=true");

        // Get all the labelList where label is null
        defaultLabelShouldNotBeFound("label.specified=false");
    }
                @Test
    @Transactional
    public void getAllLabelsByLabelContainsSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where label contains DEFAULT_LABEL
        defaultLabelShouldBeFound("label.contains=" + DEFAULT_LABEL);

        // Get all the labelList where label contains UPDATED_LABEL
        defaultLabelShouldNotBeFound("label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void getAllLabelsByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where label does not contain DEFAULT_LABEL
        defaultLabelShouldNotBeFound("label.doesNotContain=" + DEFAULT_LABEL);

        // Get all the labelList where label does not contain UPDATED_LABEL
        defaultLabelShouldBeFound("label.doesNotContain=" + UPDATED_LABEL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLabelShouldBeFound(String filter) throws Exception {
        restLabelMockMvc.perform(get("/api/labels?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(label.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)));

        // Check, that the count call also returns 1
        restLabelMockMvc.perform(get("/api/labels/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLabelShouldNotBeFound(String filter) throws Exception {
        restLabelMockMvc.perform(get("/api/labels?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLabelMockMvc.perform(get("/api/labels/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingLabel() throws Exception {
        // Get the label
        restLabelMockMvc.perform(get("/api/labels/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLabel() throws Exception {
        // Initialize the database
        labelService.save(label);

        int databaseSizeBeforeUpdate = labelRepository.findAll().size();

        // Update the label
        Label updatedLabel = labelRepository.findById(label.getId()).get();
        // Disconnect from session so that the updates on updatedLabel are not directly saved in db
        em.detach(updatedLabel);
        updatedLabel
            .label(UPDATED_LABEL);

        restLabelMockMvc.perform(put("/api/labels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLabel)))
            .andExpect(status().isOk());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate);
        Label testLabel = labelList.get(labelList.size() - 1);
        assertThat(testLabel.getLabel()).isEqualTo(UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void updateNonExistingLabel() throws Exception {
        int databaseSizeBeforeUpdate = labelRepository.findAll().size();

        // Create the Label

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLabelMockMvc.perform(put("/api/labels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(label)))
            .andExpect(status().isBadRequest());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLabel() throws Exception {
        // Initialize the database
        labelService.save(label);

        int databaseSizeBeforeDelete = labelRepository.findAll().size();

        // Delete the label
        restLabelMockMvc.perform(delete("/api/labels/{id}", label.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

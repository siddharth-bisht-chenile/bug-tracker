package com.sidbisht.jhip.defecttracker.web.rest;

import com.sidbisht.jhip.defecttracker.JhipDefectTrackerApp;
import com.sidbisht.jhip.defecttracker.domain.Ticket;
import com.sidbisht.jhip.defecttracker.domain.Label;
import com.sidbisht.jhip.defecttracker.domain.Project;
import com.sidbisht.jhip.defecttracker.domain.User;
import com.sidbisht.jhip.defecttracker.repository.TicketRepository;
import com.sidbisht.jhip.defecttracker.service.TicketService;
import com.sidbisht.jhip.defecttracker.web.rest.errors.ExceptionTranslator;
import com.sidbisht.jhip.defecttracker.service.dto.TicketCriteria;
import com.sidbisht.jhip.defecttracker.service.TicketQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.sidbisht.jhip.defecttracker.web.rest.TestUtil.sameInstant;
import static com.sidbisht.jhip.defecttracker.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link TicketResource} REST controller.
 */
@SpringBootTest(classes = JhipDefectTrackerApp.class)
public class TicketResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_DONE = false;
    private static final Boolean UPDATED_DONE = true;

    @Autowired
    private TicketRepository ticketRepository;

    @Mock
    private TicketRepository ticketRepositoryMock;

    @Mock
    private TicketService ticketServiceMock;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketQueryService ticketQueryService;

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

    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TicketResource ticketResource = new TicketResource(ticketService, ticketQueryService);
        this.restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
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
    public static Ticket createEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .dueDate(DEFAULT_DUE_DATE)
            .done(DEFAULT_DONE);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        ticket.setProject(project);
        return ticket;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createUpdatedEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .dueDate(UPDATED_DUE_DATE)
            .done(UPDATED_DONE);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        ticket.setProject(project);
        return ticket;
    }

    @BeforeEach
    public void initTest() {
        ticket = createEntity(em);
    }

    @Test
    @Transactional
    public void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket
        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticket)))
            .andExpect(status().isCreated());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate + 1);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTicket.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTicket.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testTicket.isDone()).isEqualTo(DEFAULT_DONE);
    }

    @Test
    @Transactional
    public void createTicketWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket with an existing ID
        ticket.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticket)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setTitle(null);

        // Create the Ticket, which fails.

        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticket)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setDescription(null);

        // Create the Ticket, which fails.

        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticket)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setDone(null);

        // Create the Ticket, which fails.

        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticket)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTickets() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].done").value(hasItem(DEFAULT_DONE.booleanValue())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllTicketsWithEagerRelationshipsIsEnabled() throws Exception {
        TicketResource ticketResource = new TicketResource(ticketServiceMock, ticketQueryService);
        when(ticketServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restTicketMockMvc.perform(get("/api/tickets?eagerload=true"))
        .andExpect(status().isOk());

        verify(ticketServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllTicketsWithEagerRelationshipsIsNotEnabled() throws Exception {
        TicketResource ticketResource = new TicketResource(ticketServiceMock, ticketQueryService);
            when(ticketServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restTicketMockMvc.perform(get("/api/tickets?eagerload=true"))
        .andExpect(status().isOk());

            verify(ticketServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.dueDate").value(sameInstant(DEFAULT_DUE_DATE)))
            .andExpect(jsonPath("$.done").value(DEFAULT_DONE.booleanValue()));
    }


    @Test
    @Transactional
    public void getTicketsByIdFiltering() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        Long id = ticket.getId();

        defaultTicketShouldBeFound("id.equals=" + id);
        defaultTicketShouldNotBeFound("id.notEquals=" + id);

        defaultTicketShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTicketShouldNotBeFound("id.greaterThan=" + id);

        defaultTicketShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTicketShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllTicketsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where title equals to DEFAULT_TITLE
        defaultTicketShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the ticketList where title equals to UPDATED_TITLE
        defaultTicketShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllTicketsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where title not equals to DEFAULT_TITLE
        defaultTicketShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the ticketList where title not equals to UPDATED_TITLE
        defaultTicketShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllTicketsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultTicketShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the ticketList where title equals to UPDATED_TITLE
        defaultTicketShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllTicketsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where title is not null
        defaultTicketShouldBeFound("title.specified=true");

        // Get all the ticketList where title is null
        defaultTicketShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByTitleContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where title contains DEFAULT_TITLE
        defaultTicketShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the ticketList where title contains UPDATED_TITLE
        defaultTicketShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllTicketsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where title does not contain DEFAULT_TITLE
        defaultTicketShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the ticketList where title does not contain UPDATED_TITLE
        defaultTicketShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllTicketsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where description equals to DEFAULT_DESCRIPTION
        defaultTicketShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the ticketList where description equals to UPDATED_DESCRIPTION
        defaultTicketShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTicketsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where description not equals to DEFAULT_DESCRIPTION
        defaultTicketShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the ticketList where description not equals to UPDATED_DESCRIPTION
        defaultTicketShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTicketsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTicketShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the ticketList where description equals to UPDATED_DESCRIPTION
        defaultTicketShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTicketsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where description is not null
        defaultTicketShouldBeFound("description.specified=true");

        // Get all the ticketList where description is null
        defaultTicketShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllTicketsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where description contains DEFAULT_DESCRIPTION
        defaultTicketShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the ticketList where description contains UPDATED_DESCRIPTION
        defaultTicketShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTicketsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where description does not contain DEFAULT_DESCRIPTION
        defaultTicketShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the ticketList where description does not contain UPDATED_DESCRIPTION
        defaultTicketShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllTicketsByDueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate equals to DEFAULT_DUE_DATE
        defaultTicketShouldBeFound("dueDate.equals=" + DEFAULT_DUE_DATE);

        // Get all the ticketList where dueDate equals to UPDATED_DUE_DATE
        defaultTicketShouldNotBeFound("dueDate.equals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDueDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate not equals to DEFAULT_DUE_DATE
        defaultTicketShouldNotBeFound("dueDate.notEquals=" + DEFAULT_DUE_DATE);

        // Get all the ticketList where dueDate not equals to UPDATED_DUE_DATE
        defaultTicketShouldBeFound("dueDate.notEquals=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDueDateIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate in DEFAULT_DUE_DATE or UPDATED_DUE_DATE
        defaultTicketShouldBeFound("dueDate.in=" + DEFAULT_DUE_DATE + "," + UPDATED_DUE_DATE);

        // Get all the ticketList where dueDate equals to UPDATED_DUE_DATE
        defaultTicketShouldNotBeFound("dueDate.in=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate is not null
        defaultTicketShouldBeFound("dueDate.specified=true");

        // Get all the ticketList where dueDate is null
        defaultTicketShouldNotBeFound("dueDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllTicketsByDueDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate is greater than or equal to DEFAULT_DUE_DATE
        defaultTicketShouldBeFound("dueDate.greaterThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the ticketList where dueDate is greater than or equal to UPDATED_DUE_DATE
        defaultTicketShouldNotBeFound("dueDate.greaterThanOrEqual=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDueDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate is less than or equal to DEFAULT_DUE_DATE
        defaultTicketShouldBeFound("dueDate.lessThanOrEqual=" + DEFAULT_DUE_DATE);

        // Get all the ticketList where dueDate is less than or equal to SMALLER_DUE_DATE
        defaultTicketShouldNotBeFound("dueDate.lessThanOrEqual=" + SMALLER_DUE_DATE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDueDateIsLessThanSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate is less than DEFAULT_DUE_DATE
        defaultTicketShouldNotBeFound("dueDate.lessThan=" + DEFAULT_DUE_DATE);

        // Get all the ticketList where dueDate is less than UPDATED_DUE_DATE
        defaultTicketShouldBeFound("dueDate.lessThan=" + UPDATED_DUE_DATE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDueDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where dueDate is greater than DEFAULT_DUE_DATE
        defaultTicketShouldNotBeFound("dueDate.greaterThan=" + DEFAULT_DUE_DATE);

        // Get all the ticketList where dueDate is greater than SMALLER_DUE_DATE
        defaultTicketShouldBeFound("dueDate.greaterThan=" + SMALLER_DUE_DATE);
    }


    @Test
    @Transactional
    public void getAllTicketsByDoneIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where done equals to DEFAULT_DONE
        defaultTicketShouldBeFound("done.equals=" + DEFAULT_DONE);

        // Get all the ticketList where done equals to UPDATED_DONE
        defaultTicketShouldNotBeFound("done.equals=" + UPDATED_DONE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where done not equals to DEFAULT_DONE
        defaultTicketShouldNotBeFound("done.notEquals=" + DEFAULT_DONE);

        // Get all the ticketList where done not equals to UPDATED_DONE
        defaultTicketShouldBeFound("done.notEquals=" + UPDATED_DONE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDoneIsInShouldWork() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where done in DEFAULT_DONE or UPDATED_DONE
        defaultTicketShouldBeFound("done.in=" + DEFAULT_DONE + "," + UPDATED_DONE);

        // Get all the ticketList where done equals to UPDATED_DONE
        defaultTicketShouldNotBeFound("done.in=" + UPDATED_DONE);
    }

    @Test
    @Transactional
    public void getAllTicketsByDoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList where done is not null
        defaultTicketShouldBeFound("done.specified=true");

        // Get all the ticketList where done is null
        defaultTicketShouldNotBeFound("done.specified=false");
    }

    @Test
    @Transactional
    public void getAllTicketsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        Label label = LabelResourceIT.createEntity(em);
        em.persist(label);
        em.flush();
        ticket.addLabel(label);
        ticketRepository.saveAndFlush(ticket);
        Long labelId = label.getId();

        // Get all the ticketList where label equals to labelId
        defaultTicketShouldBeFound("labelId.equals=" + labelId);

        // Get all the ticketList where label equals to labelId + 1
        defaultTicketShouldNotBeFound("labelId.equals=" + (labelId + 1));
    }


    @Test
    @Transactional
    public void getAllTicketsByProjectIsEqualToSomething() throws Exception {
        // Get already existing entity
        Project project = ticket.getProject();
        ticketRepository.saveAndFlush(ticket);
        Long projectId = project.getId();

        // Get all the ticketList where project equals to projectId
        defaultTicketShouldBeFound("projectId.equals=" + projectId);

        // Get all the ticketList where project equals to projectId + 1
        defaultTicketShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }


    @Test
    @Transactional
    public void getAllTicketsByAssignedToIsEqualToSomething() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);
        User assignedTo = UserResourceIT.createEntity(em);
        em.persist(assignedTo);
        em.flush();
        ticket.setAssignedTo(assignedTo);
        ticketRepository.saveAndFlush(ticket);
        Long assignedToId = assignedTo.getId();

        // Get all the ticketList where assignedTo equals to assignedToId
        defaultTicketShouldBeFound("assignedToId.equals=" + assignedToId);

        // Get all the ticketList where assignedTo equals to assignedToId + 1
        defaultTicketShouldNotBeFound("assignedToId.equals=" + (assignedToId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketShouldBeFound(String filter) throws Exception {
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].done").value(hasItem(DEFAULT_DONE.booleanValue())));

        // Check, that the count call also returns 1
        restTicketMockMvc.perform(get("/api/tickets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketShouldNotBeFound(String filter) throws Exception {
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketMockMvc.perform(get("/api/tickets/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTicket() throws Exception {
        // Initialize the database
        ticketService.save(ticket);

        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).get();
        // Disconnect from session so that the updates on updatedTicket are not directly saved in db
        em.detach(updatedTicket);
        updatedTicket
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .dueDate(UPDATED_DUE_DATE)
            .done(UPDATED_DONE);

        restTicketMockMvc.perform(put("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTicket)))
            .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTicket.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTicket.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTicket.isDone()).isEqualTo(UPDATED_DONE);
    }

    @Test
    @Transactional
    public void updateNonExistingTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Create the Ticket

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMockMvc.perform(put("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticket)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTicket() throws Exception {
        // Initialize the database
        ticketService.save(ticket);

        int databaseSizeBeforeDelete = ticketRepository.findAll().size();

        // Delete the ticket
        restTicketMockMvc.perform(delete("/api/tickets/{id}", ticket.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = Validator.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private ItemRequestServiceImpl service;

    @Captor
    private ArgumentCaptor<PageRequest> pageRequestArgumentCaptor;

    private AddItemRequestDto goodDto;

    @BeforeEach
    public void before() {
        goodDto = new AddItemRequestDto();
        goodDto.setRequesterId(1);
        goodDto.setDescription("Description");
    }

    @Test
    void create_shouldThrowUserNotFoundException_whenUserNotExists() {
        long requesterId = goodDto.getRequesterId();
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.create(goodDto));

        verifyNoInteractions(itemRepository, itemRepository);
    }

    @Test
    void create_shouldThrowConstraintViolationException_whenDescriptionIsNullOrBlank() {
        AddItemRequestDto wrongDto = new AddItemRequestDto();
        wrongDto.setDescription("  ");
        Validator realValidator = Validation.buildDefaultValidatorFactory().getValidator();
        ReflectionTestUtils.setField(service, "validator", realValidator);

        assertThrows(ConstraintViolationException.class, () -> service.create(wrongDto));

        wrongDto.setDescription(null);
        assertThrows(ConstraintViolationException.class, () -> service.create(wrongDto));

        verifyNoInteractions(itemRepository, userRepository, itemRepository);

        ReflectionTestUtils.setField(service, "validator", validator);

    }

    @Test
    void create_shouldReturnResult_whenUserExistsAndCreationDtoIsCorrect() {
        long requestId = 4;
        long requesterId = goodDto.getRequesterId();
        User requester = new User(requesterId, "mail@mail.ru", "userName");
        LocalDateTime pointBefore = LocalDateTime.now();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(ArgumentMatchers.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequest ir = invocationOnMock.getArgument(0, ItemRequest.class);
                    ir.setId(requestId);
                    return ir;
                });

        ItemRequestDto actual = service.create(goodDto);

        LocalDateTime pointAfter = LocalDateTime.now();

        assertEquals(requestId, actual.getId());
        assertEquals(goodDto.getDescription(), actual.getDescription());
        LocalDateTime actualTime = LocalDateTime.parse(actual.getCreated(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertTrue(actualTime.isAfter(pointBefore));
        assertTrue(actualTime.isBefore(pointAfter));

        verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void findAllRequesterRequests_shouldThrowUserNotFoundException_whenUserNotExists() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> service.findAllRequesterRequests(anyLong()));
        verifyNoInteractions(itemRequestRepository, itemRepository);
    }

    @Test
    void findAllRequesterRequests_shouldReturnEmptyList_whenNoItemRequestsFound() {
        long requesterId = 4L;
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(requesterId, Sort.by("created")))
                .thenReturn(List.of());

        List<ItemRequestWithItemsDto> actual = service.findAllRequesterRequests(requesterId);

        assertEquals(0, actual.size());
    }

    @Test
    void findAllPageable_shouldSortByCreatedDesc_whenInvoked() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), pageRequestArgumentCaptor.capture()))
                .thenReturn(Page.empty());

        service.findAllPageable(1, 0, 1);

        PageRequest actualRequest = pageRequestArgumentCaptor.getValue();
        assertEquals(Sort.sort(ItemRequest.class).by(ItemRequest::getCreated).descending(), actualRequest.getSort());
    }

    @Test
    void getRequestById_shouldThrowRequestNotFoundException_whenItemRequestNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> service.getRequestById(1, 1));
    }

    @Test
    void getRequestById_shouldReturnCorrectFields_whenInvokedAndItemRequestFound() {
        ItemRequest expectedRequest = new ItemRequest();
        expectedRequest.setId(3L);
        expectedRequest.setDescription("Description");
        expectedRequest.setCreated(LocalDateTime.now());
        List<Item> expectedItems = List.of(
                Item.builder().id(2).available(true).name("item1").build(),
                Item.builder().id(5).available(false).name("item2").build()
        );

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(expectedRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(expectedItems);

        ItemRequestWithItemsDto actual = service.getRequestById(1, 1);

        assertEquals(expectedRequest.getId(), actual.getId());
        assertEquals(expectedRequest.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), actual.getCreated());
        assertEquals(expectedItems.size(), actual.getItems().size());
        assertEquals(actual.getItems().stream().mapToLong(ItemDto::getId).sum(),
                expectedItems.stream().mapToLong(Item::getId).sum());


        verifyNoMoreInteractions(userRepository, itemRequestRepository, itemRepository);
    }


}
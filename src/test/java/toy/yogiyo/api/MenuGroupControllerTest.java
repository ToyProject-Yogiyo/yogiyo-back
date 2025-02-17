package toy.yogiyo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.common.file.ImageFileHandler;
import toy.yogiyo.common.file.ImageFileUtil;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.dto.MenuAddRequest;
import toy.yogiyo.core.menu.dto.MenuGroupAddRequest;
import toy.yogiyo.core.menu.dto.MenuGroupChangeMenuOrderRequest;
import toy.yogiyo.core.menu.dto.MenuGroupUpdateRequest;
import toy.yogiyo.core.menu.service.MenuGroupService;
import toy.yogiyo.core.menu.service.MenuService;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MenuGroupController.class)
@ExtendWith(RestDocumentationExtension.class)
class MenuGroupControllerTest {

    @MockBean
    MenuService menuService;

    @MockBean
    MenuGroupService menuGroupService;

    @MockBean
    ImageFileHandler imageFileHandler;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    final String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInByb3ZpZGVyVHlwZSI6IkRFRkFVTFQiLCJleHAiOjE2OTQ5NjY4Mjh9.Ls1wnxU41I99ijXRyKfkYI2w3kd-Q_qA2QgCLgpDTKk";

    @BeforeEach
    void beforeEach(WebApplicationContext context, RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        objectMapper = new ObjectMapper();

        new ImageFileUtil().setPath("images");
    }

    @Nested
    @DisplayName("메뉴 그룹")
    class MenuGroupTest {

        @Test
        @DisplayName("메뉴 그룹 추가")
        void add() throws Exception {
            // given
            MenuGroupAddRequest request = MenuGroupAddRequest.builder()
                    .name("순살 메뉴")
                    .content("순살")
                    .shopId(1L)
                    .build();

            given(menuGroupService.add(any())).willReturn(1L);

            // when
            ResultActions result = mockMvc.perform(post("/menu-group/add")
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andDo(print())
                    .andDo(document("menu-group/add",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 그룹 이름"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("메뉴 그룹 설명"),
                                    fieldWithPath("shopId").type(JsonFieldType.NUMBER).description("가게 ID")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("메뉴 그룹 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("메뉴 그룹 전체 조회")
        void getMenuGroups() throws Exception {
            // given
            List<Menu> menus1 = Arrays.asList(
                    Menu.builder().id(1L).name("메뉴 1").content("메뉴 1 설명").picture("image.png").price(10000).position(1).build(),
                    Menu.builder().id(2L).name("메뉴 2").content("메뉴 2 설명").picture("image.png").price(10000).position(2).build(),
                    Menu.builder().id(3L).name("메뉴 3").content("메뉴 3 설명").picture("image.png").price(10000).position(3).build()
            );
            List<Menu> menus2 = Arrays.asList(
                    Menu.builder().id(4L).name("메뉴 4").content("메뉴 4 설명").picture("image.png").price(10000).position(1).build(),
                    Menu.builder().id(5L).name("메뉴 5").content("메뉴 5 설명").picture("image.png").price(10000).position(2).build(),
                    Menu.builder().id(6L).name("메뉴 6").content("메뉴 6 설명").picture("image.png").price(10000).position(3).build()
            );

            List<MenuGroup> menuGroups = Arrays.asList(
                    MenuGroup.builder().id(1L).name("메뉴 그룹1").content("메뉴 그룹1 설명").menus(menus1).build(),
                    MenuGroup.builder().id(2L).name("메뉴 그룹2").content("메뉴 그룹2 설명").menus(menus2).build()
            );
            given(menuGroupService.findMenuGroups(anyLong())).willReturn(menuGroups);

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/menu-group/shop/{shopId}", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.menuGroups").isArray())
                    .andExpect(jsonPath("$.menuGroups.length()").value(2))
                    .andExpect(jsonPath("$.menuGroups[0].id").value(1))
                    .andExpect(jsonPath("$.menuGroups[1].id").value(2))
                    .andExpect(jsonPath("$.menuGroups[0].menus[0].id").value(1))
                    .andExpect(jsonPath("$.menuGroups[0].menus[1].id").value(2))
                    .andExpect(jsonPath("$.menuGroups[0].menus[2].id").value(3))
                    .andDo(print())
                    .andDo(document("menu-group/find-all",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("shopId").description("가게 ID")
                            ),
                            responseFields(
                                    fieldWithPath("menuGroups").type(JsonFieldType.ARRAY).description("메뉴 그룹 Array"),
                                    fieldWithPath("menuGroups[].id").type(JsonFieldType.NUMBER).description("메뉴 그룹 ID"),
                                    fieldWithPath("menuGroups[].name").type(JsonFieldType.STRING).description("메뉴 그룹 이름"),
                                    fieldWithPath("menuGroups[].content").type(JsonFieldType.STRING).description("메뉴 그룹 설명"),
                                    fieldWithPath("menuGroups[].menus").type(JsonFieldType.ARRAY).description("메뉴 Array"),
                                    fieldWithPath("menuGroups[].menus[].id").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                                    fieldWithPath("menuGroups[].menus[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                    fieldWithPath("menuGroups[].menus[].content").type(JsonFieldType.STRING).description("메뉴 설명"),
                                    fieldWithPath("menuGroups[].menus[].price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
                                    fieldWithPath("menuGroups[].menus[].picture").type(JsonFieldType.STRING).description("메뉴 사진")
                            )
                    ));
        }

        @Test
        @DisplayName("메뉴 그룹 단건 조회")
        void getMenuGroup() throws Exception {
            // given
            Shop shop = Shop.builder().id(1L).build();
            MenuGroup menuGroup = MenuGroup.builder().id(1L).shop(shop).name("메뉴 그룹1").content("메뉴 그룹1 설명").build();
            given(menuGroupService.find(anyLong())).willReturn(menuGroup);

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/menu-group/{menuGroupId}", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("메뉴 그룹1"))
                    .andExpect(jsonPath("$.content").value("메뉴 그룹1 설명"))
                    .andDo(print())
                    .andDo(document("menu-group/find-one",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuGroupId").description("메뉴 그룹 ID")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("메뉴 그룹 ID"),
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 그룹 이름"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("메뉴 그룹 설명")
                            )
                    ));
        }

        @Test
        @DisplayName("메뉴 그룹 정보 수정")
        void update() throws Exception {
            // given
            MenuGroupUpdateRequest updateRequest = MenuGroupUpdateRequest.builder()
                    .name("메뉴 그룹명 수정")
                    .content("메뉴 그룹 설명 수정").build();
            doNothing().when(menuGroupService).update(any());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/menu-group/{menuGroupId}", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(content().string("success"))
                    .andDo(print())
                    .andDo(document("menu-group/update",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuGroupId").description("메뉴 그룹 ID")
                            ),
                            requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 그룹 이름"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("메뉴 그룹 설명")
                            )
                    ));
        }

        @Test
        @DisplayName("메뉴 그룹 삭제")
        void delete() throws Exception {
            // given
            given(imageFileHandler.remove(anyString())).willReturn(true);
            doNothing().when(menuGroupService).delete(any());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/menu-group/{menuGroupId}", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt));

            // then
            result.andExpect(status().isOk())
                    .andExpect(content().string("success"))
                    .andDo(print())
                    .andDo(document("menu-group/delete",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuGroupId").description("메뉴 그룹 ID")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("메뉴 그룹 메뉴")
    class MenuGroupMenuTest {

        @Test
        @DisplayName("메뉴 그룹 메뉴 추가")
        void addMenu() throws Exception {
            // given
            MenuAddRequest request = MenuAddRequest.builder()
                    .name("양념치킨")
                    .content("양념치킨")
                    .price(19000)
                    .build();

            given(menuService.add(any())).willReturn(1L);

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/menu-group/{menuGroupId}/add-menu", 1)
                            .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andDo(print())
                    .andDo(document("menu-group/add-menu",
                            pathParameters(
                                    parameterWithName("menuGroupId").description("메뉴 그룹 ID")
                            ),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("메뉴 설명"),
                                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("메뉴 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("메뉴 그룹 메뉴 조회")
        void getMenus() throws Exception {
            // given
            List<Menu> menus = Arrays.asList(
                Menu.builder().id(1L).name("메뉴1").content("메뉴1 설명").picture("image.png").price(10000).build(),
                Menu.builder().id(2L).name("메뉴2").content("메뉴2 설명").picture("image.png").price(10000).build(),
                Menu.builder().id(3L).name("메뉴3").content("메뉴3 설명").picture("image.png").price(10000).build()
            );

            given(menuService.findMenus(anyLong())).willReturn(menus);

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/menu-group/{menuGroupId}/menu", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.menus").isArray())
                    .andExpect(jsonPath("$.menus.length()").value(3))
                    .andExpect(jsonPath("$.menus[0].name").value("메뉴1"))
                    .andExpect(jsonPath("$.menus[1].name").value("메뉴2"))
                    .andExpect(jsonPath("$.menus[2].name").value("메뉴3"))
                    .andDo(print())
                    .andDo(document("menu-group/find-menus",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuGroupId").description("메뉴 그룹 ID")
                            ),
                            responseFields(
                                    fieldWithPath("menus").type(JsonFieldType.ARRAY).description("메뉴 Array"),
                                    fieldWithPath("menus[].id").type(JsonFieldType.NUMBER).description("메뉴 ID"),
                                    fieldWithPath("menus[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
                                    fieldWithPath("menus[].content").type(JsonFieldType.STRING).description("메뉴 설명"),
                                    fieldWithPath("menus[].picture").type(JsonFieldType.STRING).description("메뉴 사진"),
                                    fieldWithPath("menus[].price").type(JsonFieldType.NUMBER).description("메뉴 가격")
                            )
                    ));
        }

        @Test
        @DisplayName("메뉴 그룹 메뉴 삭제")
        void deleteMenu() throws Exception {
            // given
            doNothing().when(menuService).delete(any());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/menu-group/delete-menu/{menuId}", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt));

            // then
            result.andExpect(status().isOk())
                    .andExpect(content().string("success"))
                    .andDo(print())
                    .andDo(document("menu-group/delete-menu",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("메뉴 그룹 메뉴 순서 변경")
        void changeOrder() throws Exception {
            // given
            MenuGroupChangeMenuOrderRequest request = MenuGroupChangeMenuOrderRequest.builder()
                    .menuIds(Arrays.asList(3L, 2L, 5L, 1L, 4L))
                    .build();
            doNothing().when(menuGroupService).changeMenuOrder(anyLong(), anyList());

            // when
            ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/menu-group/{menuGroupId}/change-menu-order", 1)
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(content().string("success"))
                    .andDo(print())
                    .andDo(document("menu-group/change-menu-order",
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access token")
                            ),
                            pathParameters(
                                    parameterWithName("menuGroupId").description("메뉴 그룹 ID")
                            ),
                            requestFields(
                                    fieldWithPath("menuIds").type(JsonFieldType.ARRAY).description("메뉴 그룹 ID Array, 순서대로 메뉴가 정렬됨")
                            )
                    ));
        }
    }
}
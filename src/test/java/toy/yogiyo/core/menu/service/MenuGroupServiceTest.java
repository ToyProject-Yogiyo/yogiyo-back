package toy.yogiyo.core.menu.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.common.exception.EntityNotFoundException;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menu.domain.MenuGroup;
import toy.yogiyo.core.menu.repository.MenuGroupRepository;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    MenuGroupService menuGroupService;

    @Mock
    MenuGroupRepository menuGroupRepository;

    @Mock
    MenuService menuService;

    @Nested
    @DisplayName("메뉴 그룹")
    class MenuGroupTest {

        @Test
        @DisplayName("메뉴 그룹 추가")
        void add() throws Exception {
            // given
            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .name("순살 메뉴")
                    .content("머스타드 + 소금 + 콜라 제공")
                    .shop(Shop.builder().id(2L).build())
                    .build();

            given(menuGroupRepository.save(any())).willReturn(menuGroup);

            // when
            Long addId = menuGroupService.add(menuGroup);

            // then
            assertThat(addId).isEqualTo(1L);
        }

        @Nested
        @DisplayName("메뉴 그룹 조회")
        class Find {

            @Test
            @DisplayName("메뉴 그룹 조회 성공")
            void success() throws Exception {
                // given
                MenuGroup menuGroup = MenuGroup.builder()
                        .id(1L)
                        .name("순살 메뉴")
                        .content("머스타드 + 소금 + 콜라 제공")
                        .shop(Shop.builder().id(2L).build())
                        .build();

                given(menuGroupRepository.findById(anyLong())).willReturn(Optional.of(menuGroup));

                // when
                MenuGroup findMenuGroup = menuGroupService.find(1L);

                // then
                assertThat(findMenuGroup).isEqualTo(menuGroup);
            }

            @Test
            @DisplayName("메뉴 그룹 조회 실패")
            void fail() throws Exception {
                // given
                given(menuGroupRepository.findById(anyLong())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> menuGroupService.find(1L))
                        .isInstanceOf(EntityNotFoundException.class);
            }
        }


        @Test
        @DisplayName("메뉴 그룹 정보 수정")
        void update() throws Exception {
            // given
            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .name("순살 메뉴")
                    .content("머스타드 + 소금 + 콜라 제공")
                    .shop(Shop.builder().id(2L).build())
                    .build();

            MenuGroup updateParam = MenuGroup.builder()
                    .id(1L)
                    .name("뼈 메뉴")
                    .content("머스타드 + 소금 + 콜라 500ml 제공")
                    .build();

            given(menuGroupRepository.findById(anyLong())).willReturn(Optional.of(menuGroup));

            // when
            menuGroupService.update(updateParam);

            // then
            assertThat(menuGroup.getName()).isEqualTo("뼈 메뉴");
            assertThat(menuGroup.getContent()).isEqualTo("머스타드 + 소금 + 콜라 500ml 제공");
        }

        @Test
        @DisplayName("메뉴 그룹 삭제")
        void delete() throws Exception {
            // given
            MenuGroup menuGroup = MenuGroup.builder()
                    .id(1L)
                    .name("순살 메뉴")
                    .content("머스타드 + 소금 + 콜라 제공")
                    .shop(Shop.builder().id(2L).build())
                    .build();
            MenuGroup deleteParam = MenuGroup.builder().id(1L).build();

            given(menuGroupRepository.findById(anyLong())).willReturn(Optional.of(menuGroup));
            doNothing().when(menuGroupRepository).delete(any());

            // when
            menuGroupService.delete(deleteParam);

            // then
            then(menuGroupRepository).should().delete(menuGroup);
        }

    }
    @Nested
    @DisplayName("메뉴 그룹 메뉴")
    class MenuGroupMenuTest {

        @Test
        @DisplayName("메뉴 그룹 메뉴 순서 변경")
        void changeMenuOrder() throws Exception {
            // given
            List<Menu> menus = Arrays.asList(
                    Menu.builder().id(1L).build(),
                    Menu.builder().id(2L).build(),
                    Menu.builder().id(3L).build(),
                    Menu.builder().id(4L).build(),
                    Menu.builder().id(5L).build()
            );

            given(menuService.findMenus(anyLong())).willReturn(menus);

            List<Menu> params = Arrays.asList(
                    Menu.builder().id(5L).build(),
                    Menu.builder().id(4L).build(),
                    Menu.builder().id(3L).build(),
                    Menu.builder().id(2L).build(),
                    Menu.builder().id(1L).build()
            );

            // when
            menuGroupService.changeMenuOrder(1L, params);

            // then
            assertThat(menus.get(0).getPosition()).isEqualTo(5);
            assertThat(menus.get(1).getPosition()).isEqualTo(4);
            assertThat(menus.get(2).getPosition()).isEqualTo(3);
            assertThat(menus.get(3).getPosition()).isEqualTo(2);
            assertThat(menus.get(4).getPosition()).isEqualTo(1);
        }

    }
}
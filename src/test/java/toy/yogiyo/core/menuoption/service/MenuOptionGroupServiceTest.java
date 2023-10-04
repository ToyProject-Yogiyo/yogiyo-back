package toy.yogiyo.core.menuoption.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toy.yogiyo.core.menu.domain.Menu;
import toy.yogiyo.core.menuoption.domain.MenuOption;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroup;
import toy.yogiyo.core.menuoption.domain.MenuOptionGroupMenu;
import toy.yogiyo.core.menuoption.domain.OptionType;
import toy.yogiyo.core.menuoption.repository.MenuOptionGroupMenuRepository;
import toy.yogiyo.core.menuoption.repository.MenuOptionGroupRepository;
import toy.yogiyo.core.shop.domain.Shop;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MenuOptionGroupServiceTest {

    @InjectMocks
    MenuOptionGroupService menuOptionGroupService;

    @Mock
    MenuOptionGroupRepository menuOptionGroupRepository;

    @Mock
    MenuOptionService menuOptionService;

    @Mock
    MenuOptionGroupMenuRepository menuOptionGroupMenuRepository;

    @Test
    @DisplayName("메뉴 옵션 그룹 추가")
    void add() throws Exception {
        // given
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .id(1L)
                .optionType(OptionType.OPTIONAL)
                .count(3)
                .shop(Shop.builder().id(1L).build())
                .menuOptions(Arrays.asList(
                        MenuOption.builder().id(1L).position(1).content("옵션1").price(1000).build(),
                        MenuOption.builder().id(2L).position(2).content("옵션2").price(1000).build(),
                        MenuOption.builder().id(3L).position(3).content("옵션3").price(1000).build(),
                        MenuOption.builder().id(4L).position(4).content("옵션4").price(1000).build(),
                        MenuOption.builder().id(5L).position(5).content("옵션5").price(1000).build()
                ))
                .build();

        given(menuOptionGroupRepository.findMaxOrder(anyLong())).willReturn(null);
        given(menuOptionService.add(any())).willReturn(1L);

        // when
        Long addedId = menuOptionGroupService.add(menuOptionGroup);

        // then
        assertThat(addedId).isEqualTo(1L);
    }

    @Test
    @DisplayName("메뉴 옵션 그룹 조회")
    void find() throws Exception {
        // given
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .id(1L)
                .build();

        given(menuOptionGroupRepository.findById(anyLong()))
                .willReturn(Optional.of(menuOptionGroup));

        // when
        MenuOptionGroup findMenuOptionGroup = menuOptionGroupService.find(1L);

        // then
        assertThat(findMenuOptionGroup).isEqualTo(menuOptionGroup);
    }

    @Test
    @DisplayName("메뉴 옵션 그룹, 옵션까지 한번에 조회")
    void findWithMenuOptions() throws Exception {
        // given
        List<MenuOption> menuOptions = Arrays.asList(
                MenuOption.builder().id(1L).position(1).content("옵션1").price(1000).build(),
                MenuOption.builder().id(2L).position(2).content("옵션2").price(1000).build(),
                MenuOption.builder().id(3L).position(3).content("옵션3").price(1000).build(),
                MenuOption.builder().id(4L).position(4).content("옵션4").price(1000).build(),
                MenuOption.builder().id(5L).position(5).content("옵션5").price(1000).build()
        );
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .id(1L)
                .menuOptions(menuOptions)
                .build();
        given(menuOptionGroupRepository.findWithMenuOptionById(anyLong()))
                .willReturn(Optional.of(menuOptionGroup));

        // when
        MenuOptionGroup findMenuOptionGroup = menuOptionGroupService.findWithMenuOptions(1L);

        // then
        assertThat(findMenuOptionGroup).isEqualTo(menuOptionGroup);
        assertThat(findMenuOptionGroup.getMenuOptions()).containsAll(menuOptions);
    }

    @Test
    @DisplayName("메뉴 옵션 그룹 정보 수정")
    void update() throws Exception {
        // given
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .id(1L)
                .build();
        given(menuOptionGroupRepository.findById(anyLong()))
                .willReturn(Optional.of(menuOptionGroup));

        MenuOptionGroup updateParam = MenuOptionGroup.builder()
                .id(1L)
                .name("옵션 그룹")
                .build();

        // when
        menuOptionGroupService.update(updateParam);

        // then
        assertThat(menuOptionGroup.getName()).isEqualTo("옵션 그룹");
    }

    @Test
    @DisplayName("메뉴 옵션 그룹 삭제")
    void delete() throws Exception {
        // given
        List<MenuOption> menuOptions = Arrays.asList(
                MenuOption.builder().id(1L).position(1).content("옵션1").price(1000).build(),
                MenuOption.builder().id(2L).position(2).content("옵션2").price(1000).build(),
                MenuOption.builder().id(3L).position(3).content("옵션3").price(1000).build(),
                MenuOption.builder().id(4L).position(4).content("옵션4").price(1000).build(),
                MenuOption.builder().id(5L).position(5).content("옵션5").price(1000).build()
        );
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .id(1L)
                .build();

        given(menuOptionGroupRepository.findById(anyLong())).willReturn(Optional.of(menuOptionGroup));
        given(menuOptionService.deleteAll(anyLong())).willReturn(menuOptions.size());
        doNothing().when(menuOptionGroupRepository).delete(any());

        // when
        menuOptionGroupService.delete(1L);

        // then
        then(menuOptionService).should().deleteAll(1L);
        then(menuOptionGroupRepository).should().delete(menuOptionGroup);
    }

    @Test
    @DisplayName("메뉴 옵션 그룹 메뉴 연결")
    void linkMenu() throws Exception {
        // given
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder().id(1L).build();
        List<Menu> menus = Arrays.asList(
                Menu.builder().id(1L).build(),
                Menu.builder().id(2L).build(),
                Menu.builder().id(3L).build(),
                Menu.builder().id(4L).build(),
                Menu.builder().id(5L).build()
        );
        List<MenuOptionGroupMenu> linkMenus = menus.stream()
                .map(menu -> MenuOptionGroupMenu.builder()
                        .menu(menu)
                        .menuOptionGroup(menuOptionGroup)
                        .build())
                .collect(Collectors.toList());

        given(menuOptionGroupRepository.findById(anyLong())).willReturn(Optional.of(menuOptionGroup));
        given(menuOptionGroupMenuRepository.saveAll(anyList())).willReturn(linkMenus);

        // when
        menuOptionGroupService.linkMenu(menuOptionGroup.getId(), menus);

        // then
        then(menuOptionGroupMenuRepository).should().saveAll(anyList());
    }
}
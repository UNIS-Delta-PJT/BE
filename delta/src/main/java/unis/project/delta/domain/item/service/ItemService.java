package unis.project.delta.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unis.project.delta.domain.item.dto.response.*;
import unis.project.delta.domain.item.entity.Item;
import unis.project.delta.domain.item.entity.UserItem;
import unis.project.delta.domain.item.repository.ItemRepository;
import unis.project.delta.domain.item.repository.UserItemRepository;
import unis.project.delta.domain.user.entity.User;
import unis.project.delta.domain.user.repository.UserRepository;
import unis.project.delta.global.exception.CustomException;
import unis.project.delta.global.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    /**
     * 코인 상점 아이템 리스트를 조회한다.
     * 각 아이템의 보유 여부(isOwned)를 함께 반환한다.
     */
    @Transactional(readOnly = true)
    public ShopResponse getShopItems(Long userId) {
        User user = findByUserId(userId);
        List<Item> allItems = itemRepository.findAll();

        List<ShopItemResponse> items = allItems.stream()
                .map(item -> {
                    boolean owned = userItemRepository.existsByUserAndItem(user, item);
                    return ShopItemResponse.of(item, owned);
                })
                .toList();

        return ShopResponse.of(user.getCoinBalance(), items);
    }

    /**
     * 코인을 차감하여 아이템을 구매한다.
     */
    @Transactional
    public BuyItemResponse buyItem(Long userId, Long itemId) {
        User user = findByUserId(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        // 중복 구매 검사
        if (userItemRepository.existsByUserAndItem(user, item)) {
            throw new CustomException(ErrorCode.ALREADY_OWNED);
        }

        // 코인 차감 (부족 시 엔티티 내부에서 예외 발생)
        user.decreaseCoinBalance(item.getPrice());

        // 보유 아이템 생성
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .build();
        userItemRepository.save(userItem);

        return BuyItemResponse.of(item, user.getCoinBalance());
    }

    /**
     * 내가 소유 중인 아이템 리스트를 조회한다.
     */
    @Transactional(readOnly = true)
    public MyItemListResponse getMyItems(Long userId) {
        User user = findByUserId(userId);
        List<UserItem> userItems = userItemRepository.findByUser(user);

        return MyItemListResponse.from(userItems);
    }

    /**
     * 아이템을 착용하거나 벗는다.
     * 착용 시 같은 종류의 기존 장착 아이템은 자동으로 해제된다.
     */
    @Transactional
    public void equipItem(Long userId, Long itemId, boolean equip) {
        User user = findByUserId(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        UserItem userItem = userItemRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_ITEM_NOT_FOUND));

        if (equip) {
            // 같은 종류(TOP, HAT 등)의 기존 장착 아이템 해제
            List<UserItem> sameTypeEquipped = userItemRepository
                    .findByUserAndIsEquippedTrueAndItem_Type(user, item.getType());
            sameTypeEquipped.forEach(UserItem::unequip);

            userItem.equip();
        } else {
            userItem.unequip();
        }
    }

    // ── private helpers ──

    private User findByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}

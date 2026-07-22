-- 1. 코인 패키지 초기 데이터 (id 명시)
INSERT INTO coin_packages (id, coin_amount, bonus_coin, price)
VALUES (1, 10, 0, 1000),
       (2, 30, 0, 3000),
       (3, 50, 0, 5000),
       (4, 100, 10, 10000),
       (5, 300, 50, 30000) ON DUPLICATE KEY
UPDATE
    coin_amount =
VALUES (coin_amount), bonus_coin =
VALUES (bonus_coin), price =
VALUES (price);

-- 2. O/X 일일 퀴즈 (테이블명: daily_quizzes 반영)
INSERT INTO daily_quizzes (id, question, correct_answer, explanation)
VALUES (1, '적금은 만기 전에 해지하면 약정 이율을 받을 수 있다.', 'X', '적금을 만기 전에 해지하면 중도해지 이율이 적용되어 약정 이율보다 낮은 이자를 받게 됩니다.'),
       (2, '체크카드는 내 계좌에 있는 돈만큼만 쓸 수 있다.', 'O', '체크카드는 연결된 계좌의 잔액 한도 내에서만 결제가 가능합니다.') ON DUPLICATE KEY
UPDATE
    question =
VALUES (question), correct_answer =
VALUES (correct_answer), explanation =
VALUES (explanation);

-- 3. 4지선다 금융 퀴즈 문항 (테이블명: finance_quizzes 반영)
INSERT INTO finance_quizzes (id, question, correct_option, explanation)
VALUES (88, '다음 중 복리 이자가 적용되는 금융 상품은?', 2,
        '정기적금은 매월 일정 금액을 불입하며, 복리 이자가 적용되어 만기 시 더 높은 수익을 기대할 수 있습니다.') ON DUPLICATE KEY
UPDATE
    question =
VALUES (question), correct_option =
VALUES (correct_option), explanation =
VALUES (explanation);

-- 4. 해당 퀴즈의 4가지 선택지
DELETE
FROM finance_quiz_options
WHERE finance_quiz_id = 88;

INSERT INTO finance_quiz_options (finance_quiz_id, option_number, content)
VALUES (88, 1, '보통예금'),
       (88, 2, '정기적금'),
       (88, 3, 'CMA'),
       (88, 4, 'MMF');

-- 기본 지출 카테고리 초기 데이터 삽입
INSERT INTO expense_categories (id, name, is_default)
VALUES (1, '식비', true),
       (2, '교통', true),
       (3, '문화', true),
       (4, '기타', true) ON DUPLICATE KEY
UPDATE
    name =
VALUES (name), is_default =
VALUES (is_default);

-- 코인 상점 기본 아이템 9종
INSERT INTO items (id, name, price, type)
VALUES (1, '분홍 뿔테 안경', 10, 'GLASSES'),
       (2, '스트라이프 리본', 12, 'TOP'),
       (3, '체크 나비넥타이', 15, 'TOP'),
       (4, '색동 복주머니', 15, 'HAT'),
       (5, '장미 꽃관', 18, 'HAT'),
       (6, '노랑 파랑 목도리', 20, 'TOP'),
       (7, '지폐 뭉치', 22, 'BOTTOM'),
       (8, '반짝이는 별구름', 25, 'HAT'),
       (9, '하트 골드 코인', 30, 'BOTTOM') ON DUPLICATE KEY
UPDATE
    name =
VALUES (name), price =
VALUES (price), type =
VALUES (type);
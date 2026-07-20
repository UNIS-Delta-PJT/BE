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

-- 4. 해당 퀴즈의 4가지 선택지 (테이블명: finance_quiz_options 반영)
-- ※ @ElementCollection 특성상 외래키(finance_quiz_id)로 연결되므로 테이블명을 정확히 맞춰야 합니다.
INSERT INTO finance_quiz_options (finance_quiz_id, option_number, content)
VALUES (88, 1, '보통예금'),
       (88, 2, '정기적금'),
       (88, 3, 'CMA'),
       (88, 4, 'MMF') ON DUPLICATE KEY
UPDATE
    content =
VALUES (content);
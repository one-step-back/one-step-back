-- 1. 팔로워 수 집계 트리거 (TBL_FOLLOW) [수정됨]
-- TBL_SUBSCRIPTION(유료)이 아니라 TBL_FOLLOW(무료) 테이블을 바라봅니다.
CREATE OR REPLACE TRIGGER TRG_UPDATE_ARTIST_FOLLOWER
    AFTER INSERT OR DELETE
    ON TBL_FOLLOW
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        -- 팔로우 시: 아티스트의 팔로워 수 + 1
        UPDATE TBL_ARTIST
        SET ARTIST_FOLLOWER_COUNT = ARTIST_FOLLOWER_COUNT + 1
        WHERE MEMBER_ID = :NEW.ARTIST_ID;
    ELSIF DELETING THEN
        -- 언팔로우 시: 아티스트의 팔로워 수 - 1
        UPDATE TBL_ARTIST
        SET ARTIST_FOLLOWER_COUNT = ARTIST_FOLLOWER_COUNT - 1
        WHERE MEMBER_ID = :OLD.ARTIST_ID;
    END IF;
END;
/

-- 2. 좋아요 수 집계 트리거
CREATE OR REPLACE TRIGGER TRG_UPDATE_FEED_LIKE_COUNT
    AFTER INSERT OR DELETE
    ON TBL_FEED_LIKE
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        UPDATE TBL_FEED
        SET FEED_LIKE_COUNT = FEED_LIKE_COUNT + 1
        WHERE ID = :NEW.FEED_ID;
    ELSIF DELETING THEN
        UPDATE TBL_FEED
        SET FEED_LIKE_COUNT = FEED_LIKE_COUNT - 1
        WHERE ID = :OLD.FEED_ID;
    END IF;
END;
/

-- 3. 댓글 수 집계 트리거 (Soft Delete 고려)
CREATE OR REPLACE TRIGGER TRG_COMMENT_CHANGE
    AFTER INSERT OR DELETE OR UPDATE OF IS_DELETED
    ON TBL_FEED_COMMENT
    FOR EACH ROW
BEGIN
    -- 1. 댓글 작성 시
    IF INSERTING THEN
        UPDATE TBL_FEED
        SET FEED_REPLY_COUNT = FEED_REPLY_COUNT + 1
        WHERE ID = :NEW.FEED_ID;

        -- 2. 댓글 Soft Delete (N -> Y)
    ELSIF UPDATING ('IS_DELETED') AND :OLD.IS_DELETED = 'N' AND :NEW.IS_DELETED = 'Y' THEN
        UPDATE TBL_FEED
        SET FEED_REPLY_COUNT = FEED_REPLY_COUNT - 1
        WHERE ID = :NEW.FEED_ID;

        -- 3. 댓글 복구 (Y -> N)
    ELSIF UPDATING ('IS_DELETED') AND :OLD.IS_DELETED = 'Y' AND :NEW.IS_DELETED = 'N' THEN
        UPDATE TBL_FEED
        SET FEED_REPLY_COUNT = FEED_REPLY_COUNT + 1
        WHERE ID = :NEW.FEED_ID;

        -- 4. 댓글 완전 삭제 (Hard Delete)
    ELSIF DELETING THEN
        UPDATE TBL_FEED
        SET FEED_REPLY_COUNT = FEED_REPLY_COUNT - 1
        WHERE ID = :OLD.FEED_ID;
    END IF;
END;
/

-- 4. 대댓글 수 집계 트리거 (Soft Delete 고려)
CREATE OR REPLACE TRIGGER TRG_REPLY_CHANGE
    AFTER INSERT OR DELETE OR UPDATE OF IS_DELETED
    ON TBL_FEED_REPLY
    FOR EACH ROW
BEGIN
    -- 1. 대댓글 작성 시
    IF INSERTING THEN
        UPDATE TBL_FEED_COMMENT
        SET REPLY_COUNT = REPLY_COUNT + 1
        WHERE ID = :NEW.COMMENT_ID;

        -- 2. 대댓글 Soft Delete (N -> Y)
    ELSIF UPDATING ('IS_DELETED') AND :OLD.IS_DELETED = 'N' AND :NEW.IS_DELETED = 'Y' THEN
        UPDATE TBL_FEED_COMMENT
        SET REPLY_COUNT = REPLY_COUNT - 1
        WHERE ID = :NEW.COMMENT_ID;

        -- 3. 대댓글 복구 (Y -> N)
    ELSIF UPDATING ('IS_DELETED') AND :OLD.IS_DELETED = 'Y' AND :NEW.IS_DELETED = 'N' THEN
        UPDATE TBL_FEED_COMMENT
        SET REPLY_COUNT = REPLY_COUNT + 1
        WHERE ID = :NEW.COMMENT_ID;

        -- 4. 대댓글 완전 삭제 (Hard Delete)
    ELSIF DELETING THEN
        UPDATE TBL_FEED_COMMENT
        SET REPLY_COUNT = REPLY_COUNT - 1
        WHERE ID = :OLD.COMMENT_ID;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_UPDATE_ARTIST_SUBSCRIPTION
    AFTER INSERT OR DELETE OR UPDATE OF STATUS
    ON TBL_SUBSCRIPTION
    FOR EACH ROW
BEGIN
    -- [CASE 1] 신규 구독 발생
    IF INSERTING THEN
        IF :NEW.STATUS = 'ACTIVE' THEN
            UPDATE TBL_ARTIST
            SET ARTIST_SUBSCRIPTION_COUNT = ARTIST_SUBSCRIPTION_COUNT + 1
            WHERE MEMBER_ID = :NEW.ARTIST_ID;
        END IF;

        -- [CASE 2] 구독 정보 삭제 (Hard Delete)
    ELSIF DELETING THEN
        IF :OLD.STATUS = 'ACTIVE' THEN
            UPDATE TBL_ARTIST
            SET ARTIST_SUBSCRIPTION_COUNT = ARTIST_SUBSCRIPTION_COUNT - 1
            WHERE MEMBER_ID = :OLD.ARTIST_ID;
        END IF;

        -- [CASE 3] 구독 상태 변경 (가장 중요)
    ELSIF UPDATING ('STATUS') THEN
        -- ACTIVE가 아니었다가 ACTIVE가 된 경우: +1
        IF :OLD.STATUS != 'ACTIVE' AND :NEW.STATUS = 'ACTIVE' THEN
            UPDATE TBL_ARTIST
            SET ARTIST_SUBSCRIPTION_COUNT = ARTIST_SUBSCRIPTION_COUNT + 1
            WHERE MEMBER_ID = :NEW.ARTIST_ID;

            -- ACTIVE였다가 다른 상태(EXPIRED, CANCELLED 등)로 변한 경우: -1
        ELSIF :OLD.STATUS = 'ACTIVE' AND :NEW.STATUS != 'ACTIVE' THEN
            UPDATE TBL_ARTIST
            SET ARTIST_SUBSCRIPTION_COUNT = ARTIST_SUBSCRIPTION_COUNT - 1
            WHERE MEMBER_ID = :NEW.ARTIST_ID;
        END IF;
    END IF;
END;
/

-- 2. 구독자 수 자동 집계 트리거 (TBL_SUBSCRIPTION 변경 시 자동 반영)
CREATE OR REPLACE TRIGGER TRG_UPDATE_MEMBERSHIP_COUNT
    AFTER INSERT OR DELETE OR UPDATE OF MEMBERSHIP_ID, STATUS
    ON TBL_SUBSCRIPTION
    FOR EACH ROW
BEGIN
    -- [CASE 1] 신규 구독 (INSERT)
    IF INSERTING THEN
        -- 상태가 ACTIVE인 경우에만 해당 멤버십 카운트 증가
        IF :NEW.STATUS = 'ACTIVE' THEN
            UPDATE TBL_MEMBERSHIP
            SET SUBSCRIBER_COUNT = SUBSCRIBER_COUNT + 1
            WHERE ID = :NEW.MEMBERSHIP_ID;
        END IF;

        -- [CASE 2] 구독 정보 완전 삭제 (DELETE)
    ELSIF DELETING THEN
        -- 삭제 전 상태가 ACTIVE였다면 카운트 감소
        IF :OLD.STATUS = 'ACTIVE' THEN
            UPDATE TBL_MEMBERSHIP
            SET SUBSCRIBER_COUNT = SUBSCRIBER_COUNT - 1
            WHERE ID = :OLD.MEMBERSHIP_ID;
        END IF;

        -- [CASE 3] 멤버십 변경 또는 상태 변경 (UPDATE)
    ELSIF UPDATING THEN
        -- 1. 이전 상태가 ACTIVE였다면, 이전 멤버십 ID의 카운트를 일단 차감
        IF :OLD.STATUS = 'ACTIVE' THEN
            UPDATE TBL_MEMBERSHIP
            SET SUBSCRIBER_COUNT = SUBSCRIBER_COUNT - 1
            WHERE ID = :OLD.MEMBERSHIP_ID;
        END IF;

        -- 2. 새로운 상태가 ACTIVE라면, 새로운 멤버십 ID의 카운트를 증가
        -- 멤버십 ID가 그대로여도 STATUS만 바뀌는 경우를 완벽히 커버한다.
        IF :NEW.STATUS = 'ACTIVE' THEN
            UPDATE TBL_MEMBERSHIP
            SET SUBSCRIBER_COUNT = SUBSCRIBER_COUNT + 1
            WHERE ID = :NEW.MEMBERSHIP_ID;
        END IF;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_SYNC_ARTIST_HAS_MEMBERSHIP
    FOR INSERT OR DELETE
    ON TBL_MEMBERSHIP
    COMPOUND TRIGGER

    /* * 처리 대상 아티스트 ID를 저장하기 위한 컬렉션 변수 선언
     */
    TYPE t_artist_ids IS TABLE OF TBL_MEMBERSHIP.ARTIST_ID%TYPE;
    v_artist_ids t_artist_ids := t_artist_ids();

    -- 행 단위 실행: 데이터 수집
AFTER EACH ROW IS
BEGIN
    IF INSERTING THEN
        -- 생성 시에는 Mutating 에러가 발생하지 않으므로 즉시 처리
        UPDATE TBL_ARTIST
        SET HAS_MEMBERSHIP = 'Y'
        WHERE MEMBER_ID = :NEW.ARTIST_ID
          AND HAS_MEMBERSHIP = 'N';
    ELSIF DELETING THEN
        -- 삭제 시에는 ID만 수집하고 조회를 뒤로 미룸
        v_artist_ids.EXTEND;
        v_artist_ids(v_artist_ids.LAST) := :OLD.ARTIST_ID;
    END IF;
END AFTER EACH ROW;

    -- 문장 단위 실행: 최종 검증 및 상태 반영
    AFTER STATEMENT IS
        v_count NUMBER;
    BEGIN
        IF v_artist_ids.COUNT > 0 THEN
            FOR i IN 1..v_artist_ids.COUNT
                LOOP
                    -- 이제 테이블이 안정된 상태이므로 조회가 가능함
                    SELECT COUNT(*)
                    INTO v_count
                    FROM TBL_MEMBERSHIP
                    WHERE ARTIST_ID = v_artist_ids(i);

                    IF v_count = 0 THEN
                        UPDATE TBL_ARTIST
                        SET HAS_MEMBERSHIP = 'N'
                        WHERE MEMBER_ID = v_artist_ids(i);
                    END IF;
                END LOOP;
        END IF;
    END AFTER STATEMENT;
    END;
/

CREATE OR REPLACE TRIGGER TRG_SYNC_MEMBER_HAS_PAYMENT
    FOR INSERT OR DELETE
    ON TBL_PAYMENT_METHOD
    COMPOUND TRIGGER

    -- 처리 대상 멤버 ID를 저장하기 위한 컬렉션 변수 선언
    TYPE t_member_ids IS TABLE OF TBL_PAYMENT_METHOD.MEMBER_ID%TYPE;
    v_member_ids t_member_ids := t_member_ids();

    -- 행 단위 실행: 데이터 수집
AFTER EACH ROW IS
BEGIN
    IF INSERTING THEN
        -- 카드 등록 시: 즉시 'Y'로 변경
        UPDATE TBL_MEMBER
        SET HAS_PAYMENT_METHOD = 'Y'
        WHERE ID = :NEW.MEMBER_ID
          AND HAS_PAYMENT_METHOD = 'N';
    ELSIF DELETING THEN
        -- 카드 삭제 시: ID 수집 후 나중에 조회
        v_member_ids.EXTEND;
        v_member_ids(v_member_ids.LAST) := :OLD.MEMBER_ID;
    END IF;
END AFTER EACH ROW;

    -- 문장 단위 실행: 최종 검증 및 상태 반영
    AFTER STATEMENT IS
        v_count NUMBER;
    BEGIN
        IF v_member_ids.COUNT > 0 THEN
            FOR i IN 1..v_member_ids.COUNT
                LOOP
                    -- 삭제 후 남은 카드가 있는지 확인
                    SELECT COUNT(*)
                    INTO v_count
                    FROM TBL_PAYMENT_METHOD
                    WHERE MEMBER_ID = v_member_ids(i);

                    -- 남은 카드가 0개면 'N'으로 변경
                    IF v_count = 0 THEN
                        UPDATE TBL_MEMBER
                        SET HAS_PAYMENT_METHOD = 'N'
                        WHERE ID = v_member_ids(i);
                    END IF;
                END LOOP;
        END IF;
    END AFTER STATEMENT;
    END;
/

-- 결제 수단 개수 제한 트리거 (인당 최대 3개)
CREATE OR REPLACE TRIGGER TRG_LIMIT_PAYMENT_METHOD
    BEFORE INSERT
    ON TBL_PAYMENT_METHOD
    FOR EACH ROW
DECLARE
    v_count NUMBER;
BEGIN
    -- 현재 등록하려는 유저의 기존 결제 수단 개수를 파악한다
    SELECT COUNT(*)
    INTO v_count
    FROM TBL_PAYMENT_METHOD
    WHERE MEMBER_ID = :NEW.MEMBER_ID;

    -- 이미 3개가 있다면 자비 없이 에러를 던져서 삽입을 막는다
    IF v_count >= 3 THEN
        RAISE_APPLICATION_ERROR(-20001,
                                'Maximum of 3 payment methods allowed per member. (MEMBER_ID: ' || :NEW.MEMBER_ID ||
                                ')');
    END IF;
END;
/
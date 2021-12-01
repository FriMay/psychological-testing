CREATE OR REPLACE FUNCTION is_contains(compared jsonb, compare jsonb) RETURNS BOOL AS
$$
DECLARE
    row jsonb;
BEGIN

    FOR row IN SELECT jsonb_array_elements(compared)
        LOOP
            if (row -> 'question_id' = compare -> 'question_id'
                AND row -> 'answer_id' = compare -> 'answer_id') THEN
                RETURN TRUE;
            END IF;
        END LOOP;

    RETURN FALSE;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION total_count(sample_test_id INT) RETURNS INT AS
$$
DECLARE
    cnt INT;
BEGIN

    SELECT count(answer)
    INTO cnt
    FROM sample_test s
             INNER JOIN jsonb_array_elements(s.tested_user_answers) AS answer ON s.id = sample_test_id;

    RETURN cnt;
END
$$ LANGUAGE plpgsql;

SELECT id, TOTAL_COUNT(id), COUNT(answer)
FROM sample_test s
         INNER JOIN JSONB_ARRAY_ELEMENTS(s.tested_user_answers) AS answer ON s.test_id = 2
WHERE IS_CONTAINS(
-----------------------Inserted from user answer
              '[
                {
                  "answer_id": 1,
                  "question_id": 2
                },
                {
                  "answer_id": 2,
                  "question_id": 3
                }
              ]'::jsonb,
              answer
          )
GROUP BY id;

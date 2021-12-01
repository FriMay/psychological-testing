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

CREATE OR REPLACE FUNCTION avg_answer(start_at INT, sample_test_id INT) RETURNS FLOAT8 AS
$$
DECLARE
    average FLOAT8;
BEGIN

    SELECT AVG((answer->'answer_id')::int) - start_at::FLOAT8
    INTO average
    FROM sample_test s
             INNER JOIN jsonb_array_elements(s.tested_user_answers) AS answer ON s.id = sample_test_id;

    RETURN average;
END
$$ LANGUAGE plpgsql;

SELECT avg_answer(0, 47);

SELECT id, TOTAL_COUNT(id), COUNT(answer)
FROM sample_test s
         INNER JOIN JSONB_ARRAY_ELEMENTS(s.tested_user_answers) AS answer ON s.test_id = 2
WHERE IS_CONTAINS(
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

SELECT avg((answer->'answer_id')::int) FROM JSONB_ARRAY_ELEMENTS('[
  {
    "answer_id": 1,
    "question_id": 2
  },
  {
    "answer_id": 2,
    "question_id": 3
  }
]'::jsonb) as answer;

SELECT id, sum((answer -> 'question_id')::int)
FROM sample_test s
         INNER JOIN JSONB_ARRAY_ELEMENTS(s.tested_user_answers) AS answer ON s.test_id = 2
GROUP BY id;

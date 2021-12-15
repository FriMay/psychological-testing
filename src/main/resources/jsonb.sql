CREATE OR REPLACE FUNCTION intersections_count(compared jsonb, compare jsonb) RETURNS INT AS
$$
DECLARE
    cnt  INT;
    row1 jsonb;
    row2 jsonb;
BEGIN

    cnt := 0;

    FOR row1 IN SELECT jsonb_array_elements(compared)
        LOOP
            FOR row2 IN SELECT jsonb_array_elements(compare)
                LOOP
                    if (row1 -> 'answer_id' = row2 -> 'answer_id') THEN
                        cnt := cnt + 1;
                    END IF;
                END LOOP;
        END LOOP;

    RETURN cnt;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION total_count(person_template_id INT) RETURNS INT AS
$$
DECLARE
    cnt INT;
BEGIN

    SELECT count(answer)
    INTO cnt
    FROM person_template pt
             INNER JOIN jsonb_array_elements(pt.answers) AS answer ON pt.id = person_template_id;

    RETURN cnt;
END
$$ LANGUAGE plpgsql;

SELECT pt.id                                                                    AS personTemplateId,
       intersections_count(pt.answers, ta.answers) / TOTAL_COUNT(pt.id)::float8 AS percentage,
       TOTAL_COUNT(pt.id)                                                       AS totalAnswers,
       intersections_count(pt.answers, ta.answers),
       ta.id                                                                    AS testAnswerId,
       t.id                                                                     AS testId
FROM test_answer ta
         INNER JOIN tested_user tu on ta.tested_user_id = tu.id
         INNER JOIN school_class sc on tu.school_class_id = :schoolClass
         INNER JOIN test t on ta.test_id = :testId
         INNER JOIN person_template pt on t.id = pt.test_id
GROUP BY t.id, ta.id, pt.id;

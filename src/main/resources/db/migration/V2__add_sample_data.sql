-- Insert sample data only if table is empty (for development)
DO $$
    BEGIN
        IF (SELECT COUNT(*) FROM tasks) = 0 THEN
            INSERT INTO tasks (title, description, completed, priority, due_date, tags) VALUES
                                                                                            ('Рефакторинг легаси кода',
                                                                                             'Переписать устаревшие модули на современный стек',
                                                                                             FALSE,
                                                                                             'URGENT',
                                                                                             NOW() + INTERVAL '10 days',
                                                                                             'technical-debt,refactoring,backend'),

                                                                                            ('Оптимизация запросов к БД',
                                                                                             'Ускорить медленные запросы, добавить индексы',
                                                                                             FALSE,
                                                                                             'HIGH',
                                                                                             NOW() + INTERVAL '4 days',
                                                                                             'database,performance,optimization'),

                                                                                            ('Написать интеграционные тесты',
                                                                                             'Покрыть тестами API эндпоинты',
                                                                                             FALSE,
                                                                                             'MEDIUM',
                                                                                             NOW() + INTERVAL '7 days',
                                                                                             'testing,quality,automation'),

                                                                                            ('Анализ конкурентов',
                                                                                             'Исследовать рынок и составить отчёт',
                                                                                             FALSE,
                                                                                             'MEDIUM',
                                                                                             NOW() + INTERVAL '14 days',
                                                                                             'marketing,research,analytics');
        END IF;
    END $$;

-- Insert attachments for existing tasks
DO $$
    DECLARE
        task_refactor_id BIGINT;
        task_optimize_id BIGINT;
    BEGIN
        -- Get task IDs dynamically
        SELECT id INTO task_refactor_id FROM tasks WHERE title = 'Рефакторинг легаси кода' LIMIT 1;
        SELECT id INTO task_optimize_id FROM tasks WHERE title = 'Оптимизация запросов к БД' LIMIT 1;

        -- Insert attachments only if they don't exist and tasks exist
        IF task_refactor_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM task_attachments WHERE task_id = task_refactor_id) THEN
            INSERT INTO task_attachments (task_id, file_name, stored_file_name, content_type, size, uploaded_at) VALUES
                (task_refactor_id,
                 'refactoring-plan.md',
                 'refactoring-plan.md',
                 'text/markdown',
                 15240,
                 NOW());
        END IF;

        IF task_optimize_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM task_attachments WHERE task_id = task_optimize_id) THEN
            INSERT INTO task_attachments (task_id, file_name, stored_file_name, content_type, size, uploaded_at) VALUES
                (task_optimize_id,
                 'queries-analysis.sql',
                 'queries-analysis.sql',
                 'application/sql',
                 8192,
                 NOW());
        END IF;
    END $$;
--Reading Goals Table
CREATE TABLE reading_goals (
    id BIGSERIAL PRIMARY KEY,
    target_books INTEGER NOT NULL,
    year INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for better query performance
CREATE INDEX idx_reading_goals_year ON reading_goals(year);
CREATE INDEX idx_reading_goals_active ON reading_goals(is_active);
CREATE INDEX idx_reading_goals_dates ON reading_goals(start_date, end_date);

-- Comments for documentation
COMMENT ON TABLE reading_goals IS 'Stores user reading goals with targets and date ranges';
COMMENT ON COLUMN reading_goals.target_books IS 'Number of books user wants to read';
COMMENT ON COLUMN reading_goals.is_active IS 'Only one goal can be active at a time';
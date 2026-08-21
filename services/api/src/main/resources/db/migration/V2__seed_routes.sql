-- V2: Seed data for Johannesburg taxi routes
INSERT INTO routes (name, origin_name, origin_lat, origin_lng, dest_name, dest_lat, dest_lng, fare, currency, active) VALUES
('Joburg CBD to Soweto', 'Joburg Park Station', -26.1929, 28.0444, 'Orlando West', -26.2485, 27.8540, 15.00, 'ZAR', true),
('Sandton to Midrand', 'Sandton City', -26.1073, 28.0517, 'Midrand Mall', -25.9989, 28.1220, 22.00, 'ZAR', true),
('Braamfontein to Rosebank', 'Braamfontein Centre', -26.1929, 28.0359, 'Rosebank Mall', -26.1392, 28.0395, 12.00, 'ZAR', true),
('Tembisa to Pretoria CBD', 'Tembisa Station', -25.9979, 28.2265, 'Pretoria Station', -25.7479, 28.1877, 35.00, 'ZAR', true),
('Alexandra to Sandton', 'Alexandra Mall', -26.1034, 28.0990, 'Sandton Gautrain', -26.1070, 28.0565, 10.00, 'ZAR', true),
('Randburg to Roodepoort', 'Randburg CBD', -26.0941, 27.9856, 'Roodepoort Station', -26.1626, 27.8717, 20.00, 'ZAR', true),
('Vereeniging to Johannesburg CBD', 'Vereenigeng Station', -26.6731, 27.9262, 'Joburg Park Station', -26.1929, 28.0444, 45.00, 'ZAR', true);

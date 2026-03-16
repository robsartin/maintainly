-- Seed script: 65 home items with service schedules for dev org
-- Target org: 019cf8ad-cb21-74ec-b48b-8962a510ac3f (Test Org / dev user)
-- Run: psql -h localhost -p 5436 -U mystuff -d mystuffdb -f doc/seed-test-data.sql

BEGIN;

-- Clear existing data for this org (cascades handle schedules/records)
DELETE FROM service_records  WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM service_schedules WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM vendor_alt_phones WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM items            WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM vendors          WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';

-- ============================================================
-- Vendors (6 home-service companies)
-- ============================================================
INSERT INTO vendors (id, organization_id, name, phone, email, created_at, updated_at) VALUES
('a0000000-0001-7000-8000-000000000001', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'AllSeason HVAC',          '(555) 201-1010', 'service@allseasonhvac.example.com',    NOW(), NOW()),
('a0000000-0001-7000-8000-000000000002', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'ClearView Plumbing',      '(555) 201-2020', 'dispatch@clearviewplumb.example.com',  NOW(), NOW()),
('a0000000-0001-7000-8000-000000000003', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'BrightSpark Electric',    '(555) 201-3030', 'jobs@brightspark.example.com',          NOW(), NOW()),
('a0000000-0001-7000-8000-000000000004', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'GreenThumb Landscaping',  '(555) 201-4040', 'info@greenthumbland.example.com',       NOW(), NOW()),
('a0000000-0001-7000-8000-000000000005', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'SafeHome Pest Control',   '(555) 201-5050', 'schedule@safehomepest.example.com',     NOW(), NOW()),
('a0000000-0001-7000-8000-000000000006', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'ProClean Chimney & Duct', '(555) 201-6060', 'book@procleanservice.example.com',      NOW(), NOW());

-- ============================================================
-- Items (65 home maintenance items)
-- ============================================================
INSERT INTO items (id, organization_id, name, location, manufacturer, model_name, model_number, serial_number, purchase_date, created_at, updated_at) VALUES
-- HVAC (5)
('b0000000-0001-7000-8000-000000000001', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Central Air Conditioner',    'Backyard',      'Carrier',       'Infinity 24',    '24ANB136A003',  'CAR-2022-58431',   '2022-05-10', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000002', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Gas Furnace',                'Basement',      'Trane',         'XR95',           'TUH1B080A9H31A','TRN-2021-77294',   '2021-10-15', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000003', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Heat Pump',                  'Utility Closet','Lennox',        'XP25',           'XP25-048-230',  'LNX-2023-41087',   '2023-03-20', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000004', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Whole-House Humidifier',     'Basement',      'Aprilaire',     'Model 600',      '600A',          'APR-2022-10553',   '2022-11-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000005', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Ductless Mini-Split',        'Sunroom',       'Mitsubishi',    'MSZ-GL12NA',     'MSZ-GL12NA-U1', 'MIT-2024-63219',   '2024-06-15', NOW(), NOW()),

-- Plumbing (6)
('b0000000-0001-7000-8000-000000000006', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Water Heater (Tank)',        'Garage',        'Rheem',         'Performance Plus','XG50T06EC36U1', 'RHM-2020-88102',   '2020-08-22', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000007', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Tankless Water Heater',      'Utility Room',  'Rinnai',        'RU199iN',        'RU199IN',       'RIN-2023-55740',   '2023-01-12', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000008', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Sump Pump',                  'Basement',      'Wayne',         'CDU980E',        'CDU980E',       'WAY-2021-30981',   '2021-04-05', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000009', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Water Softener',             'Basement',      'Culligan',      'HE Municipal',   'HE-1054',       'CUL-2022-47263',   '2022-06-18', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000010', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Whole-House Water Filter',   'Basement',      'Aquasana',      'Rhino EQ-1000',  'EQ-1000',       'AQS-2023-19847',   '2023-02-28', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000011', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Sewage Ejector Pump',        'Basement',      'Liberty Pumps', 'Pro370-Series',  'P372LE51',      'LIB-2020-62018',   '2020-11-10', NOW(), NOW()),

-- Electrical (5)
('b0000000-0001-7000-8000-000000000012', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Main Electrical Panel',      'Garage',        'Square D',      'Homeline 200A',  'HOM3060M200PC', 'SQD-2019-74530',   '2019-07-14', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000013', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Whole-House Generator',      'Side Yard',     'Generac',       'Guardian 22kW',  '7043',          'GEN-2023-82614',   '2023-09-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000014', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Whole-House Surge Protector','Main Panel',    'Eaton',         'CHSPT2ULTRA',    'CHSPT2ULTRA',   'EAT-2022-15739',   '2022-03-08', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000015', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'EV Charger',                 'Garage',        'ChargePoint',   'Home Flex',      'CPH50-NEMA14-50','CHP-2024-40291',  '2024-01-20', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000016', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Solar Panel Array',          'Roof',          'LG',            'NeON H',         'LG400N2T-A5',   'LGS-2023-91356',   '2023-04-15', NOW(), NOW()),

-- Roofing & Exterior (5)
('b0000000-0001-7000-8000-000000000017', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Asphalt Shingle Roof',       'Roof',          'GAF',           'Timberline HDZ', '?"0681-WEATHERED', 'GAF-2018-22845', '2018-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000018', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Gutters & Downspouts',       'Exterior',      'LeafGuard',     'Seamless 5-inch','LG5-AL-WHT',    'LGD-2020-37019',   '2020-09-12', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000019', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Exterior Paint',             'Exterior',      'Sherwin-Williams','Duration',      'SW-7015',       NULL,                '2021-05-20', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000020', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Vinyl Siding',               'Exterior',      'CertainTeed',   'Monogram',       'MON-D5',        'CTD-2018-56482',   '2018-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000021', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Deck',                       'Backyard',      'TimberTech',    'Azek Harvest',   'AH5420-BW',     NULL,                '2021-07-15', NOW(), NOW()),

-- Garage & Doors (3)
('b0000000-0001-7000-8000-000000000022', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Garage Door Opener',         'Garage',        'LiftMaster',    'Elite 8550W',    '8550WLB',       'LFT-2022-69174',   '2022-02-14', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000023', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Garage Door (2-Car)',        'Garage',        'Clopay',        'Gallery Steel',  'GS-2L-16x7',   'CLP-2022-28450',   '2022-02-14', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000024', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Front Entry Door',           'Front Porch',   'Therma-Tru',    'Benchmark',      'BM-FC60',       'TTR-2019-83216',   '2019-03-10', NOW(), NOW()),

-- Kitchen Appliances (6)
('b0000000-0001-7000-8000-000000000025', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Refrigerator',               'Kitchen',       'Samsung',       'Bespoke 4-Door', 'RF29BB8600QL',  'SAM-2023-10482',   '2023-08-05', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000026', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Dishwasher',                 'Kitchen',       'Bosch',         '800 Series',     'SHPM88Z75N',    'BSH-2023-54721',   '2023-08-05', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000027', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Gas Range',                  'Kitchen',       'GE Profile',    'Smart Slide-In', 'PGS930YPFS',    'GEP-2023-33098',   '2023-08-05', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000028', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Over-Range Microwave',       'Kitchen',       'GE Profile',    'Advantium 240',  'PSA9240SF5SS',  'GEP-2023-33102',   '2023-08-05', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000029', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Garbage Disposal',           'Kitchen',       'InSinkErator',  'Evolution Excel','?"?"?"78532A-ISE', 'ISE-2022-41876', '2022-12-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000030', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Range Hood',                 'Kitchen',       'Broan-NuTone',  'Elite E60E30SS', 'E60E30SS',      'BRN-2023-66014',   '2023-08-05', NOW(), NOW()),

-- Laundry (2)
('b0000000-0001-7000-8000-000000000031', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Washing Machine',            'Laundry Room',  'LG',            'WM4000HWA',      'WM4000HWA',     'LGW-2022-87530',   '2022-04-20', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000032', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Dryer',                      'Laundry Room',  'LG',            'DLEX4000W',      'DLEX4000W',     'LGD-2022-87534',   '2022-04-20', NOW(), NOW()),

-- Safety & Security (6)
('b0000000-0001-7000-8000-000000000033', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Smoke Detectors (12)',       'Whole House',   'Kidde',         'Firex i12080',   'i12080',        'KID-2021-24130',   '2021-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000034', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'CO Detectors (4)',           'Whole House',   'Kidde',         'Nighthawk',      'KN-COPP-3',     'KID-2021-24135',   '2021-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000035', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Fire Extinguishers (3)',     'Kitchen/Garage/Basement','Kidde', 'Pro 210',       '21005779',      'KID-2022-50981',   '2022-01-15', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000036', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Security System Panel',     'Front Hall',    'Ring',          'Alarm Pro',      'B08HSTJPM5',    'RNG-2023-72640',   '2023-05-10', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000037', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Radon Mitigation System',   'Basement',      'RadonAway',     'RP265',          'RP265',         'RDA-2020-18493',   '2020-03-25', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000038', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Sump Pit Backup Battery',   'Basement',      'Wayne',         'WSS30VN',        'WSS30VN',       'WAY-2021-30990',   '2021-04-05', NOW(), NOW()),

-- Fireplace & Chimney (2)
('b0000000-0001-7000-8000-000000000039', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Gas Fireplace',              'Living Room',   'Napoleon',      'Ascent X 42',    'GX42NTR-1',     'NAP-2019-45012',   '2019-11-20', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000040', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Chimney & Flue',             'Roof',          NULL,            NULL,             NULL,            NULL,                NULL,          NOW(), NOW()),

-- Windows & Insulation (3)
('b0000000-0001-7000-8000-000000000041', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Double-Hung Windows (18)',   'Whole House',   'Andersen',      'E-Series',       'E-DH-3060',     'AND-2019-60281',   '2019-03-10', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000042', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Sliding Glass Door',         'Family Room',   'Pella',         '350 Series',     'PL350-SD-72',   'PEL-2019-33748',   '2019-03-10', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000043', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Attic Insulation',           'Attic',         'Owens Corning', 'EcoTouch R-38',  'RF21',          NULL,                '2019-08-15', NOW(), NOW()),

-- Pool / Outdoor (4)
('b0000000-0001-7000-8000-000000000044', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Swimming Pool',              'Backyard',      'Pentair',       'IntelliFlo VSF', '011056',        'PEN-2020-45679',   '2020-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000045', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Pool Heater',                'Pool Equipment','Hayward',       'H250FDN',        'H250FDN',       'HAY-2020-78302',   '2020-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000046', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Irrigation System',          'Yard',          'Rain Bird',     'ESP-TM2 8-Zone', 'ESP-TM2-8',     'RBD-2021-56219',   '2021-04-15', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000047', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Outdoor Lighting System',    'Yard',          'Kichler',       'LED Low-Voltage','15820AZT',      'KCH-2022-91047',   '2022-07-10', NOW(), NOW()),

-- Flooring & Interior (4)
('b0000000-0001-7000-8000-000000000048', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Hardwood Floors',            'Main Level',    'Bruce',         'Dundee Plank',   'CB4210',        NULL,                '2019-03-10', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000049', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Carpet',                     'Bedrooms',      'Shaw',          'Caress',         'CC69B-00500',   NULL,                '2021-01-10', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000050', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Interior Paint',             'Whole House',   'Benjamin Moore','Regal Select',   'N549-OC-17',    NULL,                '2021-05-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000051', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Tile Floors (Bathrooms)',    'Bathrooms',     'Daltile',       'Rittenhouse Sq', 'X114-36MOD1P4', NULL,                '2019-03-10', NOW(), NOW()),

-- Plumbing Fixtures (4)
('b0000000-0001-7000-8000-000000000052', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Master Bath Faucets',        'Master Bath',   'Delta',         'Trinsic',        '3559-SSMPU-DST','DLT-2022-48201',   '2022-09-15', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000053', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Toilets (4)',                'Bathrooms',     'TOTO',          'Drake II',       'CST454CEFG#01', 'TOT-2019-80124',   '2019-03-10', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000054', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Kitchen Faucet',             'Kitchen',       'Moen',          'Arbor MotionSense','7594ESRS',    'MOE-2023-29615',   '2023-08-05', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000055', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Shower Valve (Master)',      'Master Bath',   'Kohler',        'Rite-Temp',      'K-304-KS-NA',   'KOH-2019-11502',   '2019-03-10', NOW(), NOW()),

-- Septic / Waste (2)
('b0000000-0001-7000-8000-000000000056', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Septic Tank',                'Backyard',      NULL,            '1500 Gallon',    NULL,            NULL,                '2010-01-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000057', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Septic Drain Field',         'Backyard',      NULL,            'Conventional',   NULL,            NULL,                '2010-01-01', NOW(), NOW()),

-- Driveway / Walkway (2)
('b0000000-0001-7000-8000-000000000058', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Asphalt Driveway',           'Front',         NULL,            NULL,             NULL,            NULL,                '2018-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000059', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Concrete Walkways',          'Front/Side',    NULL,            NULL,             NULL,            NULL,                '2018-06-01', NOW(), NOW()),

-- Miscellaneous Home Systems (6)
('b0000000-0001-7000-8000-000000000060', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Dryer Vent',                 'Laundry Room',  NULL,            '4-inch rigid',   NULL,            NULL,                '2022-04-20', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000061', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'HVAC Ductwork',              'Basement/Attic','Carrier',       'Galvanized',     NULL,            NULL,                '2019-07-14', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000062', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Bathroom Exhaust Fans (3)',  'Bathrooms',     'Panasonic',     'WhisperCeiling', 'FV-0811VFL5E',  'PAN-2021-53798',   '2021-06-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000063', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Whole-House Fan',            'Hallway Ceiling','QuietCool',    'QC CL-3100',     'CL-3100',       'QTC-2022-14680',   '2022-08-01', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000064', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Thermostat',                 'Hallway',       'Ecobee',        'SmartThermostat','EB-STATE5C-01', 'ECO-2023-81243',   '2023-03-20', NOW(), NOW()),
('b0000000-0001-7000-8000-000000000065', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Doorbell Camera',            'Front Porch',   'Ring',          'Video Doorbell 4','B09WZBPX7K',  'RNG-2023-72650',   '2023-05-10', NOW(), NOW());

-- ============================================================
-- Service Schedules
--   98%  = 64 items → Annual  (every 12 months)
--   1.5% =  1 item  → Semi-annual (every 6 months)
--   0.5% =  0 items → Quarterly (every 3 months) — round up to 1
--   Adjusted: 63 annual, 1 semi-annual, 1 quarterly = 65
-- ============================================================

-- Quarterly schedule: Swimming Pool (heavy maintenance)
INSERT INTO service_schedules (id, organization_id, item_id, preferred_vendor_id, service_type, frequency_interval, frequency_unit, first_due_date, next_due_date, active, created_at, updated_at) VALUES
('c0000000-0001-7000-8000-000000000001', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000044', NULL,
 'Pool Equipment Inspection', 3, 'months', '2026-03-01', '2026-06-01', true, NOW(), NOW());

-- Semi-annual schedule: HVAC filter + coil (high use system)
INSERT INTO service_schedules (id, organization_id, item_id, preferred_vendor_id, service_type, frequency_interval, frequency_unit, first_due_date, next_due_date, active, created_at, updated_at) VALUES
('c0000000-0001-7000-8000-000000000002', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000001',
 'a0000000-0001-7000-8000-000000000001',
 'AC Filter & Coil Service', 6, 'months', '2026-04-01', '2026-04-01', true, NOW(), NOW());

-- Annual schedules for remaining 63 items
INSERT INTO service_schedules (id, organization_id, item_id, preferred_vendor_id, service_type, frequency_interval, frequency_unit, first_due_date, next_due_date, active, created_at, updated_at) VALUES
-- HVAC annual
('c0000000-0001-7000-8000-000000000003', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000002', 'a0000000-0001-7000-8000-000000000001',
 'Furnace Tune-Up', 1, 'years', '2026-09-15', '2026-09-15', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000004', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000003', 'a0000000-0001-7000-8000-000000000001',
 'Heat Pump Inspection', 1, 'years', '2026-10-01', '2026-10-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000005', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000004', 'a0000000-0001-7000-8000-000000000001',
 'Humidifier Pad Replacement', 1, 'years', '2026-10-15', '2026-10-15', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000006', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000005', 'a0000000-0001-7000-8000-000000000001',
 'Mini-Split Cleaning', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
-- Plumbing annual
('c0000000-0001-7000-8000-000000000007', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000006', 'a0000000-0001-7000-8000-000000000002',
 'Tank Flush & Anode Check', 1, 'years', '2026-08-01', '2026-08-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000008', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000007', 'a0000000-0001-7000-8000-000000000002',
 'Tankless Descaling', 1, 'years', '2027-01-01', '2027-01-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000009', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000008', 'a0000000-0001-7000-8000-000000000002',
 'Sump Pump Test & Clean', 1, 'years', '2026-03-15', '2026-03-15', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000010', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000009', 'a0000000-0001-7000-8000-000000000002',
 'Water Softener Resin Service', 1, 'years', '2026-06-01', '2026-06-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000011', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000010', 'a0000000-0001-7000-8000-000000000002',
 'Filter Cartridge Replacement', 1, 'years', '2027-02-01', '2027-02-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000012', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000011', 'a0000000-0001-7000-8000-000000000002',
 'Ejector Pump Inspection', 1, 'years', '2026-11-01', '2026-11-01', true, NOW(), NOW()),
-- Electrical annual
('c0000000-0001-7000-8000-000000000013', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000012', 'a0000000-0001-7000-8000-000000000003',
 'Panel & Breaker Inspection', 1, 'years', '2026-07-01', '2026-07-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000014', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000013', 'a0000000-0001-7000-8000-000000000003',
 'Generator Load-Bank Test', 1, 'years', '2026-09-01', '2026-09-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000015', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000014', 'a0000000-0001-7000-8000-000000000003',
 'Surge Protector Indicator Check', 1, 'years', '2026-03-01', '2026-03-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000016', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000015', 'a0000000-0001-7000-8000-000000000003',
 'EV Charger Inspection', 1, 'years', '2027-01-15', '2027-01-15', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000017', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000016', 'a0000000-0001-7000-8000-000000000003',
 'Solar Panel Cleaning & Check', 1, 'years', '2026-04-15', '2026-04-15', true, NOW(), NOW()),
-- Roofing & Exterior annual
('c0000000-0001-7000-8000-000000000018', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000017', NULL,
 'Roof Inspection', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000019', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000018', NULL,
 'Gutter Cleaning & Flush', 1, 'years', '2026-11-01', '2026-11-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000020', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000019', NULL,
 'Exterior Paint Touch-Up', 1, 'years', '2026-06-01', '2026-06-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000021', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000020', NULL,
 'Siding Wash & Inspect', 1, 'years', '2026-05-15', '2026-05-15', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000022', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000021', NULL,
 'Deck Clean & Seal', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
-- Garage & Doors annual
('c0000000-0001-7000-8000-000000000023', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000022', NULL,
 'Opener Lubrication & Safety Test', 1, 'years', '2026-04-01', '2026-04-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000024', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000023', NULL,
 'Door Balance & Seal Check', 1, 'years', '2026-04-01', '2026-04-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000025', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000024', NULL,
 'Weatherstrip Replacement', 1, 'years', '2026-10-01', '2026-10-01', true, NOW(), NOW()),
-- Kitchen Appliances annual
('c0000000-0001-7000-8000-000000000026', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000025', NULL,
 'Condenser Coil Cleaning', 1, 'years', '2026-08-01', '2026-08-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000027', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000026', NULL,
 'Dishwasher Filter & Spray Arm Clean', 1, 'years', '2026-08-01', '2026-08-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000028', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000027', NULL,
 'Gas Line & Burner Inspection', 1, 'years', '2026-08-01', '2026-08-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000029', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000028', NULL,
 'Grease Filter & Vent Cleaning', 1, 'years', '2026-08-01', '2026-08-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000030', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000029', 'a0000000-0001-7000-8000-000000000002',
 'Disposal Cleaning & Blade Check', 1, 'years', '2026-12-01', '2026-12-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000031', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000030', NULL,
 'Range Hood Filter Cleaning', 1, 'years', '2026-08-01', '2026-08-01', true, NOW(), NOW()),
-- Laundry annual
('c0000000-0001-7000-8000-000000000032', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000031', NULL,
 'Washer Drum Clean & Hose Inspect', 1, 'years', '2026-04-01', '2026-04-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000033', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000032', NULL,
 'Lint Trap & Vent Inspection', 1, 'years', '2026-04-01', '2026-04-01', true, NOW(), NOW()),
-- Safety & Security annual
('c0000000-0001-7000-8000-000000000034', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000033', NULL,
 'Smoke Detector Battery & Test', 1, 'years', '2026-11-01', '2026-11-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000035', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000034', NULL,
 'CO Detector Battery & Test', 1, 'years', '2026-11-01', '2026-11-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000036', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000035', NULL,
 'Fire Extinguisher Pressure Check', 1, 'years', '2027-01-15', '2027-01-15', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000037', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000036', NULL,
 'Security System Battery & Sensor Test', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000038', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000037', NULL,
 'Radon Level Test', 1, 'years', '2026-03-01', '2026-03-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000039', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000038', NULL,
 'Backup Battery Load Test', 1, 'years', '2026-04-01', '2026-04-01', true, NOW(), NOW()),
-- Fireplace & Chimney annual
('c0000000-0001-7000-8000-000000000040', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000039', 'a0000000-0001-7000-8000-000000000006',
 'Gas Fireplace Service', 1, 'years', '2026-09-01', '2026-09-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000041', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000040', 'a0000000-0001-7000-8000-000000000006',
 'Chimney Sweep & Inspection', 1, 'years', '2026-09-15', '2026-09-15', true, NOW(), NOW()),
-- Windows & Insulation annual
('c0000000-0001-7000-8000-000000000042', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000041', NULL,
 'Window Seal & Caulk Inspection', 1, 'years', '2026-10-01', '2026-10-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000043', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000042', NULL,
 'Track & Roller Lubrication', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000044', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000043', NULL,
 'Insulation Depth Check', 1, 'years', '2026-10-15', '2026-10-15', true, NOW(), NOW()),
-- Pool & Outdoor annual (pool already has quarterly; these are separate annual tasks)
('c0000000-0001-7000-8000-000000000045', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000045', NULL,
 'Pool Heater Tune-Up', 1, 'years', '2026-04-15', '2026-04-15', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000046', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000046', 'a0000000-0001-7000-8000-000000000004',
 'Irrigation Winterize & Start-Up', 1, 'years', '2026-04-01', '2026-04-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000047', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000047', 'a0000000-0001-7000-8000-000000000003',
 'Outdoor Lighting Fixture Check', 1, 'years', '2026-07-01', '2026-07-01', true, NOW(), NOW()),
-- Flooring & Interior annual
('c0000000-0001-7000-8000-000000000048', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000048', NULL,
 'Hardwood Floor Condition Check', 1, 'years', '2026-06-01', '2026-06-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000049', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000049', NULL,
 'Professional Carpet Cleaning', 1, 'years', '2027-01-01', '2027-01-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000050', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000050', NULL,
 'Interior Touch-Up & Inspection', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000051', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000051', NULL,
 'Grout Cleaning & Seal', 1, 'years', '2027-03-01', '2027-03-01', true, NOW(), NOW()),
-- Plumbing Fixtures annual
('c0000000-0001-7000-8000-000000000052', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000052', 'a0000000-0001-7000-8000-000000000002',
 'Faucet Aerator & Cartridge Check', 1, 'years', '2026-09-01', '2026-09-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000053', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000053', 'a0000000-0001-7000-8000-000000000002',
 'Toilet Flapper & Fill Valve Check', 1, 'years', '2026-09-01', '2026-09-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000054', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000054', 'a0000000-0001-7000-8000-000000000002',
 'Kitchen Faucet O-Ring & Hose Check', 1, 'years', '2026-08-01', '2026-08-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000055', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000055', 'a0000000-0001-7000-8000-000000000002',
 'Shower Valve Cartridge Inspection', 1, 'years', '2026-09-01', '2026-09-01', true, NOW(), NOW()),
-- Septic annual
('c0000000-0001-7000-8000-000000000056', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000056', 'a0000000-0001-7000-8000-000000000002',
 'Septic Tank Pump-Out', 1, 'years', '2026-06-01', '2026-06-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000057', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000057', NULL,
 'Drain Field Inspection', 1, 'years', '2026-06-01', '2026-06-01', true, NOW(), NOW()),
-- Driveway & Walkway annual
('c0000000-0001-7000-8000-000000000058', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000058', NULL,
 'Driveway Seal & Crack Fill', 1, 'years', '2026-06-01', '2026-06-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000059', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000059', NULL,
 'Walkway Leveling Check', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
-- Miscellaneous annual
('c0000000-0001-7000-8000-000000000060', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000060', 'a0000000-0001-7000-8000-000000000006',
 'Dryer Vent Cleaning', 1, 'years', '2026-04-01', '2026-04-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000061', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000061', 'a0000000-0001-7000-8000-000000000006',
 'Duct Cleaning & Sanitizing', 1, 'years', '2026-10-01', '2026-10-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000062', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000062', NULL,
 'Exhaust Fan Cleaning & Motor Check', 1, 'years', '2026-06-01', '2026-06-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000063', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000063', NULL,
 'Whole-House Fan Shutter & Belt Check', 1, 'years', '2026-05-01', '2026-05-01', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000064', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000064', NULL,
 'Thermostat Calibration', 1, 'years', '2026-03-20', '2026-03-20', true, NOW(), NOW()),
('c0000000-0001-7000-8000-000000000065', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'b0000000-0001-7000-8000-000000000065', NULL,
 'Doorbell Camera Firmware & Clean', 1, 'years', '2026-05-10', '2026-05-10', true, NOW(), NOW());

COMMIT;

-- Verify counts
SELECT 'Items: ' || COUNT(*) FROM items WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f'
UNION ALL
SELECT 'Schedules: ' || COUNT(*) FROM service_schedules WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f'
UNION ALL
SELECT 'Vendors: ' || COUNT(*) FROM vendors WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f'
UNION ALL
SELECT 'Annual: ' || COUNT(*) FROM service_schedules WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f' AND frequency_unit = 'years'
UNION ALL
SELECT 'Semi-annual: ' || COUNT(*) FROM service_schedules WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f' AND frequency_unit = 'months' AND frequency_interval = 6
UNION ALL
SELECT 'Quarterly: ' || COUNT(*) FROM service_schedules WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f' AND frequency_unit = 'months' AND frequency_interval = 3;

-- Seed script: 65 home items with service schedules and history
-- Target org: 019cf8ad-cb21-74ec-b48b-8962a510ac3f (Test Org / dev user)
-- Usage: sed 's/019cf8ad-cb21-74ec-b48b-8962a510ac3f/YOUR_ORG_ID/g' doc/seed-test-data.sql | psql ...

BEGIN;

-- Clear existing data for this org
DELETE FROM service_records  WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM service_schedules WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM vendor_alt_phones WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM items            WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';
DELETE FROM vendors          WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f';

-- Assign rob.sartin@gmail.com to this org (creates user if absent)
INSERT INTO app_users (id, username, organization_id, created_at, updated_at)
VALUES (gen_random_uuid(), 'rob.sartin@gmail.com',
        '019cf8ad-cb21-74ec-b48b-8962a510ac3f', NOW(), NOW())
ON CONFLICT (username) DO UPDATE
    SET organization_id = EXCLUDED.organization_id;

-- ============================================================
-- Vendors (7: 1 system-managed Unknown + 6 real)
-- ============================================================
INSERT INTO vendors (id, organization_id, name, system_managed, created_at, updated_at) VALUES
('a0000000-0001-7000-8000-000000000000', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'Unknown Vendor', true, NOW(), NOW());

INSERT INTO vendors (id, organization_id, name, phone, email, system_managed, created_at, updated_at) VALUES
('a0000000-0001-7000-8000-000000000001', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'AllSeason HVAC',          '(555) 201-1010', 'service@allseasonhvac.example.com',    false, NOW(), NOW()),
('a0000000-0001-7000-8000-000000000002', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'ClearView Plumbing',      '(555) 201-2020', 'dispatch@clearviewplumb.example.com',  false, NOW(), NOW()),
('a0000000-0001-7000-8000-000000000003', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'BrightSpark Electric',    '(555) 201-3030', 'jobs@brightspark.example.com',          false, NOW(), NOW()),
('a0000000-0001-7000-8000-000000000004', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'GreenThumb Landscaping',  '(555) 201-4040', 'info@greenthumbland.example.com',       false, NOW(), NOW()),
('a0000000-0001-7000-8000-000000000005', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'SafeHome Pest Control',   '(555) 201-5050', 'schedule@safehomepest.example.com',     false, NOW(), NOW()),
('a0000000-0001-7000-8000-000000000006', '019cf8ad-cb21-74ec-b48b-8962a510ac3f',
 'ProClean Chimney & Duct', '(555) 201-6060', 'book@procleanservice.example.com',      false, NOW(), NOW());

-- ============================================================
-- Items (65 home maintenance items)
-- ============================================================
INSERT INTO items (id, organization_id, name, location, manufacturer, model_name, model_number, serial_number, purchase_date, created_at, updated_at) VALUES
('b0000000-0001-7000-8000-000000000001','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Central Air Conditioner','Backyard','Carrier','Infinity 24','24ANB136A003','CAR-2022-58431','2022-05-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000002','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Gas Furnace','Basement','Trane','XR95','TUH1B080A9H31A','TRN-2021-77294','2021-10-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000003','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Heat Pump','Utility Closet','Lennox','XP25','XP25-048-230','LNX-2023-41087','2023-03-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000004','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Whole-House Humidifier','Basement','Aprilaire','Model 600','600A','APR-2022-10553','2022-11-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000005','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Ductless Mini-Split','Sunroom','Mitsubishi','MSZ-GL12NA','MSZ-GL12NA-U1','MIT-2024-63219','2024-06-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000006','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Water Heater (Tank)','Garage','Rheem','Performance Plus','XG50T06EC36U1','RHM-2020-88102','2020-08-22',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000007','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Tankless Water Heater','Utility Room','Rinnai','RU199iN','RU199IN','RIN-2023-55740','2023-01-12',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000008','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Sump Pump','Basement','Wayne','CDU980E','CDU980E','WAY-2021-30981','2021-04-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000009','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Water Softener','Basement','Culligan','HE Municipal','HE-1054','CUL-2022-47263','2022-06-18',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000010','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Whole-House Water Filter','Basement','Aquasana','Rhino EQ-1000','EQ-1000','AQS-2023-19847','2023-02-28',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000011','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Sewage Ejector Pump','Basement','Liberty Pumps','Pro370-Series','P372LE51','LIB-2020-62018','2020-11-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000012','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Main Electrical Panel','Garage','Square D','Homeline 200A','HOM3060M200PC','SQD-2019-74530','2019-07-14',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000013','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Whole-House Generator','Side Yard','Generac','Guardian 22kW','7043','GEN-2023-82614','2023-09-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000014','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Whole-House Surge Protector','Main Panel','Eaton','CHSPT2ULTRA','CHSPT2ULTRA','EAT-2022-15739','2022-03-08',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000015','019cf8ad-cb21-74ec-b48b-8962a510ac3f','EV Charger','Garage','ChargePoint','Home Flex','CPH50-NEMA14-50','CHP-2024-40291','2024-01-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000016','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Solar Panel Array','Roof','LG','NeON H','LG400N2T-A5','LGS-2023-91356','2023-04-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000017','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Asphalt Shingle Roof','Roof','GAF','Timberline HDZ','0681-WW','GAF-2018-22845','2018-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000018','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Gutters & Downspouts','Exterior','LeafGuard','Seamless 5-inch','LG5-AL-WHT','LGD-2020-37019','2020-09-12',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000019','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Exterior Paint','Exterior','Sherwin-Williams','Duration','SW-7015',NULL,'2021-05-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000020','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Vinyl Siding','Exterior','CertainTeed','Monogram','MON-D5','CTD-2018-56482','2018-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000021','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Deck','Backyard','TimberTech','Azek Harvest','AH5420-BW',NULL,'2021-07-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000022','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Garage Door Opener','Garage','LiftMaster','Elite 8550W','8550WLB','LFT-2022-69174','2022-02-14',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000023','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Garage Door (2-Car)','Garage','Clopay','Gallery Steel','GS-2L-16x7','CLP-2022-28450','2022-02-14',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000024','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Front Entry Door','Front Porch','Therma-Tru','Benchmark','BM-FC60','TTR-2019-83216','2019-03-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000025','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Refrigerator','Kitchen','Samsung','Bespoke 4-Door','RF29BB8600QL','SAM-2023-10482','2023-08-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000026','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Dishwasher','Kitchen','Bosch','800 Series','SHPM88Z75N','BSH-2023-54721','2023-08-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000027','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Gas Range','Kitchen','GE Profile','Smart Slide-In','PGS930YPFS','GEP-2023-33098','2023-08-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000028','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Over-Range Microwave','Kitchen','GE Profile','Advantium 240','PSA9240SF5SS','GEP-2023-33102','2023-08-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000029','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Garbage Disposal','Kitchen','InSinkErator','Evolution Excel','78532A-ISE','ISE-2022-41876','2022-12-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000030','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Range Hood','Kitchen','Broan-NuTone','Elite E60E30SS','E60E30SS','BRN-2023-66014','2023-08-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000031','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Washing Machine','Laundry Room','LG','WM4000HWA','WM4000HWA','LGW-2022-87530','2022-04-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000032','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Dryer','Laundry Room','LG','DLEX4000W','DLEX4000W','LGD-2022-87534','2022-04-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000033','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Smoke Detectors (12)','Whole House','Kidde','Firex i12080','i12080','KID-2021-24130','2021-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000034','019cf8ad-cb21-74ec-b48b-8962a510ac3f','CO Detectors (4)','Whole House','Kidde','Nighthawk','KN-COPP-3','KID-2021-24135','2021-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000035','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Fire Extinguishers (3)','Kitchen/Garage/Basement','Kidde','Pro 210','21005779','KID-2022-50981','2022-01-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000036','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Security System Panel','Front Hall','Ring','Alarm Pro','B08HSTJPM5','RNG-2023-72640','2023-05-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000037','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Radon Mitigation System','Basement','RadonAway','RP265','RP265','RDA-2020-18493','2020-03-25',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000038','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Sump Pit Backup Battery','Basement','Wayne','WSS30VN','WSS30VN','WAY-2021-30990','2021-04-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000039','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Gas Fireplace','Living Room','Napoleon','Ascent X 42','GX42NTR-1','NAP-2019-45012','2019-11-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000040','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Chimney & Flue','Roof',NULL,NULL,NULL,NULL,NULL,NOW(),NOW()),
('b0000000-0001-7000-8000-000000000041','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Double-Hung Windows (18)','Whole House','Andersen','E-Series','E-DH-3060','AND-2019-60281','2019-03-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000042','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Sliding Glass Door','Family Room','Pella','350 Series','PL350-SD-72','PEL-2019-33748','2019-03-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000043','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Attic Insulation','Attic','Owens Corning','EcoTouch R-38','RF21',NULL,'2019-08-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000044','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Swimming Pool','Backyard','Pentair','IntelliFlo VSF','011056','PEN-2020-45679','2020-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000045','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Pool Heater','Pool Equipment','Hayward','H250FDN','H250FDN','HAY-2020-78302','2020-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000046','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Irrigation System','Yard','Rain Bird','ESP-TM2 8-Zone','ESP-TM2-8','RBD-2021-56219','2021-04-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000047','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Outdoor Lighting System','Yard','Kichler','LED Low-Voltage','15820AZT','KCH-2022-91047','2022-07-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000048','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Hardwood Floors','Main Level','Bruce','Dundee Plank','CB4210',NULL,'2019-03-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000049','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Carpet','Bedrooms','Shaw','Caress','CC69B-00500',NULL,'2021-01-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000050','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Interior Paint','Whole House','Benjamin Moore','Regal Select','N549-OC-17',NULL,'2021-05-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000051','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Tile Floors (Bathrooms)','Bathrooms','Daltile','Rittenhouse Sq','X114-36MOD1P4',NULL,'2019-03-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000052','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Master Bath Faucets','Master Bath','Delta','Trinsic','3559-SSMPU-DST','DLT-2022-48201','2022-09-15',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000053','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Toilets (4)','Bathrooms','TOTO','Drake II','CST454CEFG#01','TOT-2019-80124','2019-03-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000054','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Kitchen Faucet','Kitchen','Moen','Arbor MotionSense','7594ESRS','MOE-2023-29615','2023-08-05',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000055','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Shower Valve (Master)','Master Bath','Kohler','Rite-Temp','K-304-KS-NA','KOH-2019-11502','2019-03-10',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000056','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Septic Tank','Backyard',NULL,'1500 Gallon',NULL,NULL,'2010-01-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000057','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Septic Drain Field','Backyard',NULL,'Conventional',NULL,NULL,'2010-01-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000058','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Asphalt Driveway','Front',NULL,NULL,NULL,NULL,'2018-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000059','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Concrete Walkways','Front/Side',NULL,NULL,NULL,NULL,'2018-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000060','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Dryer Vent','Laundry Room',NULL,'4-inch rigid',NULL,NULL,'2022-04-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000061','019cf8ad-cb21-74ec-b48b-8962a510ac3f','HVAC Ductwork','Basement/Attic','Carrier','Galvanized',NULL,NULL,'2019-07-14',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000062','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Bathroom Exhaust Fans (3)','Bathrooms','Panasonic','WhisperCeiling','FV-0811VFL5E','PAN-2021-53798','2021-06-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000063','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Whole-House Fan','Hallway Ceiling','QuietCool','QC CL-3100','CL-3100','QTC-2022-14680','2022-08-01',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000064','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Thermostat','Hallway','Ecobee','SmartThermostat','EB-STATE5C-01','ECO-2023-81243','2023-03-20',NOW(),NOW()),
('b0000000-0001-7000-8000-000000000065','019cf8ad-cb21-74ec-b48b-8962a510ac3f','Doorbell Camera','Front Porch','Ring','Video Doorbell 4','B09WZBPX7K','RNG-2023-72650','2023-05-10',NOW(),NOW());

-- ============================================================
-- Service Schedules (65 total: 63 annual, 1 semi-annual, 1 quarterly)
-- All schedules now require a vendor (NOT NULL constraint)
-- Items without a specific vendor use Unknown Vendor
-- ============================================================

-- Helper: v1=HVAC, v2=Plumbing, v3=Electric, v4=Landscape, v5=Pest, v6=Chimney, v0=Unknown
-- Quarterly: Swimming Pool
INSERT INTO service_schedules (id, organization_id, item_id, preferred_vendor_id, service_type, frequency_interval, frequency_unit, first_due_date, next_due_date, active, created_at, updated_at) VALUES
('c0000000-0001-7000-8000-000000000001','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000044','a0000000-0001-7000-8000-000000000000','Pool Equipment Inspection',3,'months','2026-03-01','2026-06-01',true,NOW(),NOW()),
-- Semi-annual: AC
('c0000000-0001-7000-8000-000000000002','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',6,'months','2026-04-01','2026-04-01',true,NOW(),NOW()),
-- Annual (63)
('c0000000-0001-7000-8000-000000000003','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',1,'years','2026-09-15','2026-09-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000004','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Heat Pump Inspection',1,'years','2026-10-01','2026-10-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000005','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000004','a0000000-0001-7000-8000-000000000001','Humidifier Pad Replacement',1,'years','2026-10-15','2026-10-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000006','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000005','a0000000-0001-7000-8000-000000000001','Mini-Split Cleaning',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000007','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000006','a0000000-0001-7000-8000-000000000002','Tank Flush & Anode Check',1,'years','2026-08-01','2026-08-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000008','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000007','a0000000-0001-7000-8000-000000000002','Tankless Descaling',1,'years','2027-01-01','2027-01-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000009','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000008','a0000000-0001-7000-8000-000000000002','Sump Pump Test & Clean',1,'years','2026-03-15','2026-03-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000010','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000009','a0000000-0001-7000-8000-000000000002','Water Softener Resin Service',1,'years','2026-06-01','2026-06-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000011','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000010','a0000000-0001-7000-8000-000000000002','Filter Cartridge Replacement',1,'years','2027-02-01','2027-02-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000012','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000011','a0000000-0001-7000-8000-000000000002','Ejector Pump Inspection',1,'years','2026-11-01','2026-11-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000013','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',1,'years','2026-07-01','2026-07-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000014','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Generator Load-Bank Test',1,'years','2026-09-01','2026-09-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000015','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000014','a0000000-0001-7000-8000-000000000003','Surge Protector Indicator Check',1,'years','2026-03-01','2026-03-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000016','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000015','a0000000-0001-7000-8000-000000000003','EV Charger Inspection',1,'years','2027-01-15','2027-01-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000017','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000016','a0000000-0001-7000-8000-000000000003','Solar Panel Cleaning & Check',1,'years','2026-04-15','2026-04-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000018','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','a0000000-0001-7000-8000-000000000000','Roof Inspection',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000019','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Gutter Cleaning & Flush',1,'years','2026-11-01','2026-11-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000020','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000019','a0000000-0001-7000-8000-000000000000','Exterior Paint Touch-Up',1,'years','2026-06-01','2026-06-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000021','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000020','a0000000-0001-7000-8000-000000000000','Siding Wash & Inspect',1,'years','2026-05-15','2026-05-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000022','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000021','a0000000-0001-7000-8000-000000000000','Deck Clean & Seal',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000023','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000022','a0000000-0001-7000-8000-000000000000','Opener Lubrication & Safety Test',1,'years','2026-04-01','2026-04-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000024','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000023','a0000000-0001-7000-8000-000000000000','Door Balance & Seal Check',1,'years','2026-04-01','2026-04-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000025','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000024','a0000000-0001-7000-8000-000000000000','Weatherstrip Replacement',1,'years','2026-10-01','2026-10-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000026','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000025','a0000000-0001-7000-8000-000000000000','Condenser Coil Cleaning',1,'years','2026-08-01','2026-08-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000027','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000026','a0000000-0001-7000-8000-000000000000','Dishwasher Filter & Spray Arm Clean',1,'years','2026-08-01','2026-08-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000028','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000027','a0000000-0001-7000-8000-000000000000','Gas Line & Burner Inspection',1,'years','2026-08-01','2026-08-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000029','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000028','a0000000-0001-7000-8000-000000000000','Grease Filter & Vent Cleaning',1,'years','2026-08-01','2026-08-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000030','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000029','a0000000-0001-7000-8000-000000000002','Disposal Cleaning & Blade Check',1,'years','2026-12-01','2026-12-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000031','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000030','a0000000-0001-7000-8000-000000000000','Range Hood Filter Cleaning',1,'years','2026-08-01','2026-08-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000032','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000031','a0000000-0001-7000-8000-000000000000','Washer Drum Clean & Hose Inspect',1,'years','2026-04-01','2026-04-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000033','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000032','a0000000-0001-7000-8000-000000000006','Lint Trap & Vent Inspection',1,'years','2026-04-01','2026-04-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000034','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000033','a0000000-0001-7000-8000-000000000000','Smoke Detector Battery & Test',1,'years','2026-11-01','2026-11-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000035','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000034','a0000000-0001-7000-8000-000000000000','CO Detector Battery & Test',1,'years','2026-11-01','2026-11-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000036','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000035','a0000000-0001-7000-8000-000000000000','Fire Extinguisher Pressure Check',1,'years','2027-01-15','2027-01-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000037','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000036','a0000000-0001-7000-8000-000000000000','Security System Battery & Sensor Test',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000038','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000037','a0000000-0001-7000-8000-000000000000','Radon Level Test',1,'years','2026-03-01','2026-03-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000039','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000038','a0000000-0001-7000-8000-000000000000','Backup Battery Load Test',1,'years','2026-04-01','2026-04-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000040','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000039','a0000000-0001-7000-8000-000000000006','Gas Fireplace Service',1,'years','2026-09-01','2026-09-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000041','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',1,'years','2026-09-15','2026-09-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000042','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000000','Window Seal & Caulk Inspection',1,'years','2026-10-01','2026-10-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000043','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000042','a0000000-0001-7000-8000-000000000000','Track & Roller Lubrication',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000044','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000043','a0000000-0001-7000-8000-000000000000','Insulation Depth Check',1,'years','2026-10-15','2026-10-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000045','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000045','a0000000-0001-7000-8000-000000000000','Pool Heater Tune-Up',1,'years','2026-04-15','2026-04-15',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000046','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000046','a0000000-0001-7000-8000-000000000004','Irrigation Winterize & Start-Up',1,'years','2026-04-01','2026-04-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000047','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000047','a0000000-0001-7000-8000-000000000003','Outdoor Lighting Fixture Check',1,'years','2026-07-01','2026-07-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000048','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000048','a0000000-0001-7000-8000-000000000000','Hardwood Floor Condition Check',1,'years','2026-06-01','2026-06-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000049','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000049','a0000000-0001-7000-8000-000000000000','Professional Carpet Cleaning',1,'years','2027-01-01','2027-01-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000050','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000050','a0000000-0001-7000-8000-000000000000','Interior Touch-Up & Inspection',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000051','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000051','a0000000-0001-7000-8000-000000000000','Grout Cleaning & Seal',1,'years','2027-03-01','2027-03-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000052','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000052','a0000000-0001-7000-8000-000000000002','Faucet Aerator & Cartridge Check',1,'years','2026-09-01','2026-09-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000053','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000053','a0000000-0001-7000-8000-000000000002','Toilet Flapper & Fill Valve Check',1,'years','2026-09-01','2026-09-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000054','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000054','a0000000-0001-7000-8000-000000000002','Kitchen Faucet O-Ring & Hose Check',1,'years','2026-08-01','2026-08-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000055','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000055','a0000000-0001-7000-8000-000000000002','Shower Valve Cartridge Inspection',1,'years','2026-09-01','2026-09-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000056','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',1,'years','2026-06-01','2026-06-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000057','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000057','a0000000-0001-7000-8000-000000000000','Drain Field Inspection',1,'years','2026-06-01','2026-06-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000058','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000058','a0000000-0001-7000-8000-000000000000','Driveway Seal & Crack Fill',1,'years','2026-06-01','2026-06-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000059','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000059','a0000000-0001-7000-8000-000000000000','Walkway Leveling Check',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000060','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000060','a0000000-0001-7000-8000-000000000006','Dryer Vent Cleaning',1,'years','2026-04-01','2026-04-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000061','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000061','a0000000-0001-7000-8000-000000000006','Duct Cleaning & Sanitizing',1,'years','2026-10-01','2026-10-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000062','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000062','a0000000-0001-7000-8000-000000000000','Exhaust Fan Cleaning & Motor Check',1,'years','2026-06-01','2026-06-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000063','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000063','a0000000-0001-7000-8000-000000000000','Whole-House Fan Shutter & Belt Check',1,'years','2026-05-01','2026-05-01',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000064','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000064','a0000000-0001-7000-8000-000000000000','Thermostat Calibration',1,'years','2026-03-20','2026-03-20',true,NOW(),NOW()),
('c0000000-0001-7000-8000-000000000065','019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000065','a0000000-0001-7000-8000-000000000000','Doorbell Camera Firmware & Clean',1,'years','2026-05-10','2026-05-10',true,NOW(),NOW());

-- ============================================================
-- Service Records: up to 10 years of history for key items
-- Generates realistic annual/semi-annual completion records
-- ============================================================
INSERT INTO service_records (id, organization_id, item_id, service_schedule_id, vendor_id, service_type, data_entry_timestamp, service_date, summary, cost, created_at, updated_at) VALUES
-- Furnace: annual since 2017
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2017-09-20','Annual tune-up, replaced igniter',185.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2018-09-18','Annual tune-up, all clear',150.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2019-09-22','Annual tune-up, cleaned flame sensor',155.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2020-09-15','Annual tune-up, replaced blower belt',175.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2021-09-14','Annual tune-up, all clear',160.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2022-09-19','Annual tune-up, tightened gas valve',165.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2023-09-18','Annual tune-up, replaced filter',155.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2024-09-16','Annual tune-up, all clear',170.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000002','c0000000-0001-7000-8000-000000000003','a0000000-0001-7000-8000-000000000001','Furnace Tune-Up',NOW(),'2025-09-15','Annual tune-up, cleaned burners',175.00,NOW(),NOW()),
-- AC: semi-annual since 2022
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','c0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',NOW(),'2022-10-05','Fall coil cleaning',120.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','c0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',NOW(),'2023-04-10','Spring filter & coil service',125.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','c0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',NOW(),'2023-10-02','Fall coil cleaning',120.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','c0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',NOW(),'2024-04-08','Spring filter & coil service',130.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','c0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',NOW(),'2024-10-07','Fall coil cleaning, replaced capacitor',195.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','c0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',NOW(),'2025-04-14','Spring filter & coil service',130.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000001','c0000000-0001-7000-8000-000000000002','a0000000-0001-7000-8000-000000000001','AC Filter & Coil Service',NOW(),'2025-10-06','Fall coil cleaning',125.00,NOW(),NOW()),
-- Water Heater: annual since 2020
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000006','c0000000-0001-7000-8000-000000000007','a0000000-0001-7000-8000-000000000002','Tank Flush & Anode Check',NOW(),'2021-08-10','Flushed sediment, anode OK',95.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000006','c0000000-0001-7000-8000-000000000007','a0000000-0001-7000-8000-000000000002','Tank Flush & Anode Check',NOW(),'2022-08-08','Flushed sediment, replaced anode rod',145.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000006','c0000000-0001-7000-8000-000000000007','a0000000-0001-7000-8000-000000000002','Tank Flush & Anode Check',NOW(),'2023-08-14','Flushed sediment, anode OK',100.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000006','c0000000-0001-7000-8000-000000000007','a0000000-0001-7000-8000-000000000002','Tank Flush & Anode Check',NOW(),'2024-08-12','Flushed sediment, replaced T&P valve',165.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000006','c0000000-0001-7000-8000-000000000007','a0000000-0001-7000-8000-000000000002','Tank Flush & Anode Check',NOW(),'2025-08-11','Flushed sediment, anode OK',105.00,NOW(),NOW()),
-- Chimney: annual since 2019
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','c0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',NOW(),'2019-09-10','Level 1 inspection, light creosote',225.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','c0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',NOW(),'2020-09-14','Level 1 inspection, all clear',200.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','c0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',NOW(),'2021-09-13','Level 1 inspection, replaced cap screen',250.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','c0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',NOW(),'2022-09-12','Level 1 inspection, all clear',210.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','c0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',NOW(),'2023-09-11','Level 2 inspection, mortar repair',475.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','c0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',NOW(),'2024-09-09','Level 1 inspection, all clear',215.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000040','c0000000-0001-7000-8000-000000000041','a0000000-0001-7000-8000-000000000006','Chimney Sweep & Inspection',NOW(),'2025-09-15','Level 1 inspection, light creosote',225.00,NOW(),NOW()),
-- Septic: annual since 2016
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2016-06-15','Pumped 1500 gal, baffle intact',350.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2017-06-12','Pumped, replaced outlet tee',425.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2018-06-11','Pumped 1500 gal, all clear',360.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2019-06-10','Pumped 1500 gal, all clear',365.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2020-06-08','Pumped 1500 gal, all clear',370.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2021-06-14','Pumped, minor effluent filter cleaning',390.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2022-06-13','Pumped 1500 gal, all clear',380.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2023-06-12','Pumped 1500 gal, all clear',385.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2024-06-10','Pumped 1500 gal, replaced riser lid',420.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000056','c0000000-0001-7000-8000-000000000056','a0000000-0001-7000-8000-000000000002','Septic Tank Pump-Out',NOW(),'2025-06-09','Pumped 1500 gal, all clear',395.00,NOW(),NOW()),
-- Electrical Panel: annual since 2019
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','c0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',NOW(),'2019-07-15','Inspected, torqued connections',175.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','c0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',NOW(),'2020-07-13','Inspected, replaced GFCI breaker',225.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','c0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',NOW(),'2021-07-12','Inspected, all clear',180.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','c0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',NOW(),'2022-07-11','Inspected, thermal scan clean',185.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','c0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',NOW(),'2023-07-10','Inspected, all clear',190.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','c0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',NOW(),'2024-07-08','Inspected, torqued connections',195.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000012','c0000000-0001-7000-8000-000000000013','a0000000-0001-7000-8000-000000000003','Panel & Breaker Inspection',NOW(),'2025-07-14','Inspected, all clear',200.00,NOW(),NOW()),
-- Roof: annual since 2018
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','c0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Roof Inspection',NOW(),'2019-05-06','New roof inspection, all clear',0.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','c0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Roof Inspection',NOW(),'2020-05-04','Inspected, minor flashing repair',275.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','c0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Roof Inspection',NOW(),'2021-05-03','Inspected, replaced 3 shingles',185.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','c0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Roof Inspection',NOW(),'2022-05-09','Inspected, all clear',150.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','c0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Roof Inspection',NOW(),'2023-05-08','Inspected, resealed vent boot',195.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','c0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Roof Inspection',NOW(),'2024-05-06','Inspected, all clear',160.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000017','c0000000-0001-7000-8000-000000000018','a0000000-0001-7000-8000-000000000000','Roof Inspection',NOW(),'2025-05-05','Inspected, all clear',165.00,NOW(),NOW()),
-- Dryer Vent: annual since 2022
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000060','c0000000-0001-7000-8000-000000000060','a0000000-0001-7000-8000-000000000006','Dryer Vent Cleaning',NOW(),'2023-04-03','Cleaned 15 ft run, heavy lint buildup',125.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000060','c0000000-0001-7000-8000-000000000060','a0000000-0001-7000-8000-000000000006','Dryer Vent Cleaning',NOW(),'2024-04-01','Cleaned, moderate lint',120.00,NOW(),NOW()),
(gen_random_uuid(),'019cf8ad-cb21-74ec-b48b-8962a510ac3f','b0000000-0001-7000-8000-000000000060','c0000000-0001-7000-8000-000000000060','a0000000-0001-7000-8000-000000000006','Dryer Vent Cleaning',NOW(),'2025-04-07','Cleaned, light lint, all clear',115.00,NOW(),NOW());

COMMIT;

-- Verify counts
SELECT 'Items: ' || COUNT(*) FROM items WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f'
UNION ALL
SELECT 'Schedules: ' || COUNT(*) FROM service_schedules WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f'
UNION ALL
SELECT 'Vendors: ' || COUNT(*) FROM vendors WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f'
UNION ALL
SELECT 'Records: ' || COUNT(*) FROM service_records WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f'
UNION ALL
SELECT 'System vendors: ' || COUNT(*) FROM vendors WHERE organization_id = '019cf8ad-cb21-74ec-b48b-8962a510ac3f' AND system_managed = true;

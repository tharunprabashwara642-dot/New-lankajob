-- =========================================================================
-- LankaJobs Production Database Schema & Setup Script
-- Sri Lanka Job Discovery & Manual Verified Advertising Platform
-- Run this in your Supabase SQL Editor (https://supabase.com/dashboard/project/_/sql)
-- =========================================================================

-- 1. Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. USER PROFILES
CREATE TABLE IF NOT EXISTS profiles (
    id TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL,
    phone TEXT,
    avatar_url TEXT,
    preferred_location TEXT,
    preferred_categories JSONB DEFAULT '[]'::jsonb,
    role TEXT NOT NULL DEFAULT 'user', -- 'user', 'admin'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. ADVERTISEMENT PACKAGES TABLE
CREATE TABLE IF NOT EXISTS ad_packages (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT DEFAULT '',
    price_lkr INTEGER NOT NULL,
    duration_days INTEGER NOT NULL,
    features JSONB NOT NULL DEFAULT '[]'::jsonb,
    is_popular BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    sort_order INTEGER DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Insert / Upsert Default LankaJobs Packages
INSERT INTO ad_packages (id, name, description, price_lkr, duration_days, features, is_popular, is_active, sort_order)
VALUES 
('pkg_basic', 'Starter Listing', 'Standard entry visibility for short-term hiring or local course intakes', 2500, 14, '["Standard placement on Home feed", "Visible for 14 days", "Contact phone & email display", "Verified advertiser badge"]'::jsonb, false, true, 1),
('pkg_standard', 'Professional Banner', 'Prominent banner placement for professional institutes and corporate hiring', 6000, 30, '["Prominent banner in search and category feeds", "Visible for 30 days", "Official website URL link button", "High-priority Admin verification", "Social & newsletter highlight"]'::jsonb, true, true, 2),
('pkg_premium', 'Featured Enterprise', 'Maximum reach with top sticky pinning across home, search, and push alerts', 12500, 45, '["Top pinned placement on Home & search results", "Visible for 45 days", "Official website & phone direct connection", "Priority employer badge", "Targeted push announcement to jobseekers", "Weekly reach analytics"]'::jsonb, false, true, 3)
ON CONFLICT (id) DO UPDATE 
SET name = EXCLUDED.name, description = EXCLUDED.description, price_lkr = EXCLUDED.price_lkr, duration_days = EXCLUDED.duration_days, features = EXCLUDED.features, is_popular = EXCLUDED.is_popular, is_active = EXCLUDED.is_active;

-- 4. ADVERTISEMENTS TABLE
CREATE TABLE IF NOT EXISTS advertisements (
    id TEXT PRIMARY KEY DEFAULT 'ad_' || SUBSTRING(gen_random_uuid()::text, 1, 8),
    user_id TEXT,
    title TEXT NOT NULL,
    organization_name TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL,
    location TEXT NOT NULL,
    contact_name TEXT,
    contact_phone TEXT,
    contact_email TEXT,
    website_url TEXT,
    image_url TEXT,
    package_id TEXT REFERENCES ad_packages(id) ON DELETE SET NULL,
    package_name TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PAYMENT_SUBMITTED', -- DRAFT, PENDING_PAYMENT, PAYMENT_SUBMITTED, PAYMENT_VERIFIED, PENDING_APPROVAL, APPROVED, REJECTED, EXPIRED, CANCELLED
    payment_method TEXT DEFAULT 'Bank Deposit / Slip',
    payment_reference TEXT,
    amount_paid_lkr INTEGER NOT NULL DEFAULT 0,
    rejection_reason TEXT,
    created_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
    valid_until BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. MANUAL PAYMENTS TABLE (Source of Truth for Verification)
CREATE TABLE IF NOT EXISTS payments (
    id TEXT PRIMARY KEY DEFAULT 'pay_' || SUBSTRING(gen_random_uuid()::text, 1, 8),
    advertisement_id TEXT REFERENCES advertisements(id) ON DELETE CASCADE,
    user_id TEXT NOT NULL,
    package_id TEXT REFERENCES ad_packages(id) ON DELETE SET NULL,
    package_name TEXT NOT NULL,
    amount_lkr INTEGER NOT NULL,
    payment_method TEXT NOT NULL DEFAULT 'Bank Deposit / Slip',
    payment_reference TEXT NOT NULL,
    payment_date TEXT NOT NULL,
    receipt_url TEXT,
    status TEXT NOT NULL DEFAULT 'SUBMITTED', -- PENDING, SUBMITTED, VERIFIED, REJECTED
    rejection_reason TEXT,
    verified_by TEXT,
    verified_at TIMESTAMP WITH TIME ZONE,
    submitted_at BIGINT NOT NULL DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 6. PAYMENT SETTINGS TABLE (Bank instructions editable from Admin Web)
CREATE TABLE IF NOT EXISTS payment_settings (
    id TEXT PRIMARY KEY DEFAULT 'default',
    bank_name TEXT NOT NULL DEFAULT 'Bank of Ceylon / Commercial Bank',
    branch TEXT NOT NULL DEFAULT 'Corporate Branch, Colombo',
    branch_code TEXT NOT NULL DEFAULT '001',
    account_holder_name TEXT NOT NULL DEFAULT 'LankaJobs (Pvt) Ltd',
    account_number TEXT NOT NULL DEFAULT '8012345678',
    payment_instructions TEXT NOT NULL DEFAULT 'Transfer the exact package amount via online banking or bank deposit slip. Upload your payment receipt and enter the transaction reference.',
    payment_note TEXT NOT NULL DEFAULT 'Verification takes between 15-60 minutes during business hours.',
    qr_code_url TEXT,
    supported_methods JSONB DEFAULT '["Direct Bank Deposit", "Online Banking Slip Transfer", "ATM / CDM Deposit"]'::jsonb,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

INSERT INTO payment_settings (id, bank_name, branch, branch_code, account_holder_name, accountNumber, payment_instructions, payment_note)
VALUES (
    'default',
    'Bank of Ceylon / Commercial Bank',
    'Corporate Branch, Colombo 01',
    '001',
    'LankaJobs (Pvt) Ltd',
    '8012345678',
    'Transfer the exact package amount via online banking or physical deposit slip. Upload clear photo/screenshot of slip.',
    'Verification takes between 15-60 minutes during standard business hours.'
)
ON CONFLICT (id) DO NOTHING;

-- 7. APP SETTINGS & TELEGRAM CONFIGURATION
CREATE TABLE IF NOT EXISTS app_settings (
    id TEXT PRIMARY KEY DEFAULT 'default',
    app_name TEXT NOT NULL DEFAULT 'LankaJobs',
    support_email TEXT NOT NULL DEFAULT 'support@lankajobs.lk',
    support_phone TEXT NOT NULL DEFAULT '+94 11 234 5678',
    announcement_banner TEXT,
    maintenance_mode BOOLEAN NOT NULL DEFAULT false,
    privacy_url TEXT NOT NULL DEFAULT 'https://lankajobs.lk/privacy',
    terms_url TEXT NOT NULL DEFAULT 'https://lankajobs.lk/terms',
    telegram_bot_token TEXT,
    telegram_chat_id TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

INSERT INTO app_settings (id, app_name, support_email, support_phone, maintenance_mode)
VALUES ('default', 'LankaJobs', 'support@lankajobs.lk', '+94 11 234 5678', false)
ON CONFLICT (id) DO NOTHING;

-- 8. AUDIT LOGS TABLE
CREATE TABLE IF NOT EXISTS audit_logs (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    admin_email TEXT NOT NULL,
    action TEXT NOT NULL, -- PAYMENT_VERIFIED, PAYMENT_REJECTED, AD_APPROVED, AD_REJECTED, PACKAGE_UPDATED, SETTINGS_UPDATED
    entity TEXT NOT NULL, -- payment, advertisement, package, settings, job
    entity_id TEXT NOT NULL,
    old_status TEXT,
    new_status TEXT,
    reason TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 9. JOBS TABLE
CREATE TABLE IF NOT EXISTS jobs (
    id TEXT PRIMARY KEY DEFAULT 'job_' || SUBSTRING(gen_random_uuid()::text, 1, 8),
    title TEXT NOT NULL,
    company_id TEXT NOT NULL,
    company_name TEXT NOT NULL,
    company_logo_url TEXT,
    category_id TEXT NOT NULL,
    category_name TEXT NOT NULL,
    location TEXT NOT NULL,
    district TEXT NOT NULL,
    employment_type TEXT NOT NULL DEFAULT 'FULL_TIME',
    experience_level TEXT NOT NULL DEFAULT 'MID_LEVEL',
    workplace_type TEXT NOT NULL DEFAULT 'ON_SITE',
    salary_min INTEGER,
    salary_max INTEGER,
    salary_currency TEXT NOT NULL DEFAULT 'LKR',
    is_salary_negotiable BOOLEAN NOT NULL DEFAULT false,
    description TEXT NOT NULL,
    responsibilities_joined TEXT NOT NULL DEFAULT '',
    requirements_joined TEXT NOT NULL DEFAULT '',
    benefits_joined TEXT NOT NULL DEFAULT '',
    posted_date TEXT NOT NULL DEFAULT 'Today',
    closing_date TEXT,
    is_featured BOOLEAN NOT NULL DEFAULT false,
    is_urgent BOOLEAN NOT NULL DEFAULT false,
    application_url TEXT,
    application_email TEXT,
    source TEXT NOT NULL DEFAULT 'LankaJobs',
    views_count INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 10. SAVED JOBS TABLE
CREATE TABLE IF NOT EXISTS saved_jobs (
    id TEXT PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id TEXT NOT NULL,
    job_id TEXT REFERENCES jobs(id) ON DELETE CASCADE,
    saved_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Row Level Security (RLS) policies
ALTER TABLE ad_packages ENABLE ROW LEVEL SECURITY;
ALTER TABLE advertisements ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE payment_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE app_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE jobs ENABLE ROW LEVEL SECURITY;
ALTER TABLE saved_jobs ENABLE ROW LEVEL SECURITY;

-- Read policies
CREATE POLICY "Allow public read on active packages" ON ad_packages FOR SELECT USING (is_active = true);
CREATE POLICY "Allow public read on approved ads" ON advertisements FOR SELECT USING (status = 'APPROVED');
CREATE POLICY "Allow public insert on ads" ON advertisements FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow users read own ads" ON advertisements FOR SELECT USING (true);
CREATE POLICY "Allow public read on payment settings" ON payment_settings FOR SELECT USING (true);
CREATE POLICY "Allow public read on app settings" ON app_settings FOR SELECT USING (true);
CREATE POLICY "Allow public read on jobs" ON jobs FOR SELECT USING (true);

-- Payments RLS
CREATE POLICY "Allow users insert payments" ON payments FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow users read own payments" ON payments FOR SELECT USING (true);

-- Admin full access
CREATE POLICY "Allow all on ad_packages for admin" ON ad_packages FOR ALL USING (true);
CREATE POLICY "Allow all on advertisements for admin" ON advertisements FOR ALL USING (true);
CREATE POLICY "Allow all on payments for admin" ON payments FOR ALL USING (true);
CREATE POLICY "Allow all on payment_settings for admin" ON payment_settings FOR ALL USING (true);
CREATE POLICY "Allow all on app_settings for admin" ON app_settings FOR ALL USING (true);
CREATE POLICY "Allow all on audit_logs for admin" ON audit_logs FOR ALL USING (true);
CREATE POLICY "Allow all on jobs for admin" ON jobs FOR ALL USING (true);

-- =========================================================================
-- Supabase Storage Buckets Setup Script
-- =========================================================================
-- 1. payment-receipts (Private - Admin & Uploader only)
-- 2. advertisement-images (Public)
-- 3. profile-images (Public)
--
-- In your Supabase Dashboard -> Storage:
-- Create bucket "payment-receipts" (Public: false)
-- Create bucket "advertisement-images" (Public: true)
-- Create bucket "profile-images" (Public: true)

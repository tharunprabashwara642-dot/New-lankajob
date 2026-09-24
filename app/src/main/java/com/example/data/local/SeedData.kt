package com.example.data.local

import com.example.domain.model.AdStatus
import com.example.domain.model.Advertisement
import com.example.domain.model.AdvertisementPackage
import com.example.domain.model.EmploymentType
import com.example.domain.model.ExperienceLevel
import com.example.domain.model.Job
import com.example.domain.model.JobCategory
import com.example.domain.model.WorkplaceType

object SeedData {

    val categories = listOf(
        JobCategory("cat-it", "IT & Software", 42, "laptop"),
        JobCategory("cat-acc", "Accounting & Finance", 28, "calculator"),
        JobCategory("cat-sales", "Sales & Marketing", 35, "trending_up"),
        JobCategory("cat-eng", "Engineering", 19, "build"),
        JobCategory("cat-edu", "Education & Teaching", 14, "school"),
        JobCategory("cat-med", "Healthcare & Medical", 16, "local_hospital"),
        JobCategory("cat-admin", "Administration & HR", 22, "business"),
        JobCategory("cat-intern", "Internships", 31, "workspace_premium"),
        JobCategory("cat-remote", "Remote Jobs", 25, "home_work")
    )

    val locations = listOf(
        "Colombo",
        "Kandy",
        "Galle",
        "Gampaha",
        "Kurunegala",
        "Negombo",
        "Kegalle",
        "Jaffna",
        "Matara",
        "Remote"
    )

    val packages = listOf(
        AdvertisementPackage(
            id = "pkg_basic",
            name = "Starter Listing",
            priceLkr = 2500,
            durationDays = 14,
            features = listOf(
                "Standard placement on Home feed",
                "Visible for 14 days",
                "Contact phone & email display",
                "Verified advertiser badge"
            ),
            isPopular = false
        ),
        AdvertisementPackage(
            id = "pkg_standard",
            name = "Professional Banner",
            priceLkr = 6000,
            durationDays = 30,
            features = listOf(
                "Prominent banner in search and category feeds",
                "Visible for 30 days",
                "Official website URL link button",
                "High-priority Admin verification",
                "Social & newsletter highlight"
            ),
            isPopular = true
        ),
        AdvertisementPackage(
            id = "pkg_premium",
            name = "Featured Enterprise",
            priceLkr = 12500,
            durationDays = 45,
            features = listOf(
                "Top pinned placement on Home & search results",
                "Visible for 45 days",
                "Official website & phone direct connection",
                "Targeted push alert to jobseekers",
                "Weekly analytics & reach reporting"
            ),
            isPopular = false
        )
    )

    val approvedAds = listOf(
        Advertisement(
            id = "ad_001",
            title = "Diploma in Cloud Computing & DevOps (Intake 2026)",
            description = "Enroll in Sri Lanka's leading hands-on Cloud DevOps engineering certification. Weekend batches available with industry mentors from top tech firms.",
            organizationName = "Colombo Tech Academy",
            imageUrl = null,
            websiteUrl = "https://example.com/colombo-tech-devops",
            contactPhone = "+94 11 234 5678",
            contactEmail = "admissions@colombotech.lk",
            location = "Colombo",
            category = "IT & Software",
            packageId = "pkg_standard",
            packageName = "Professional Banner",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 3,
            validUntil = System.currentTimeMillis() + 86400000 * 27
        ),
        Advertisement(
            id = "ad_002",
            title = "Kandy Career Fair & Walk-in Interviews 2026",
            description = "Over 40 leading local and international companies recruiting for Engineering, IT, Finance, and Hospitality roles at the Grand Central Hall.",
            organizationName = "Central Province Job Council",
            imageUrl = null,
            websiteUrl = "https://example.com/kandy-career-fair",
            contactPhone = "+94 81 223 9900",
            contactEmail = "info@centralcareers.lk",
            location = "Kandy",
            category = "Recruitment & Events",
            packageId = "pkg_premium",
            packageName = "Featured Enterprise",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 5,
            validUntil = System.currentTimeMillis() + 86400000 * 20
        ),
        Advertisement(
            id = "ad_003",
            title = "Chartered Accounting Fast-Track Revision Program",
            description = "Intensive weekend preparation for Corporate Level examinations by seasoned CAs. Hybrid classroom in Nawala and online streaming.",
            organizationName = "Lanka Finance Institute",
            imageUrl = null,
            websiteUrl = null,
            contactPhone = "+94 11 789 0123",
            contactEmail = "support@lankafinance.lk",
            location = "Colombo",
            category = "Accounting & Finance",
            packageId = "pkg_basic",
            packageName = "Starter Listing",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 2,
            validUntil = System.currentTimeMillis() + 86400000 * 12
        ),
        Advertisement(
            id = "ad_004",
            title = "Southern Maritime Logistics & Shipping Diploma",
            description = "Port management, customs documentation, and supply chain shipping logistics certified program at Galle Maritime Training Centre.",
            organizationName = "Galle Maritime Academy",
            imageUrl = null,
            websiteUrl = "https://example.com/galle-maritime",
            contactPhone = "+94 91 224 8877",
            contactEmail = "info@gallemaritime.lk",
            location = "Galle",
            category = "Engineering",
            packageId = "pkg_standard",
            packageName = "Professional Banner",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 4,
            validUntil = System.currentTimeMillis() + 86400000 * 26
        ),
        Advertisement(
            id = "ad_005",
            title = "Full-Stack AI & Mobile Development Masterclass",
            description = "Learn Kotlin Jetpack Compose, Spring Boot microservices, and AI integrations. Live weekend training with real corporate projects.",
            organizationName = "Silicon Colombo Hub",
            imageUrl = null,
            websiteUrl = "https://example.com/silicon-colombo-ai",
            contactPhone = "+94 11 556 7890",
            contactEmail = "apply@siliconcolombo.lk",
            location = "Colombo",
            category = "IT & Software",
            packageId = "pkg_premium",
            packageName = "Featured Enterprise",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 1,
            validUntil = System.currentTimeMillis() + 86400000 * 44
        ),
        Advertisement(
            id = "ad_006",
            title = "Wayamba Agri-Tech & Smart Irrigation Workshop",
            description = "Practical hydroponics, greenhouse automation, and export agro-business coaching by the North Western Agro Council.",
            organizationName = "Wayamba Agricultural Forum",
            imageUrl = null,
            websiteUrl = null,
            contactPhone = "+94 37 222 4110",
            contactEmail = "contact@wayambaagro.lk",
            location = "Kurunegala",
            category = "Engineering",
            packageId = "pkg_basic",
            packageName = "Starter Listing",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 6,
            validUntil = System.currentTimeMillis() + 86400000 * 14
        ),
        Advertisement(
            id = "ad_007",
            title = "Northern Tech Career Summit 2026",
            description = "Walk-in interviews, code clinics, and software engineering hiring fair at the Jaffna Heritage Convention Hall.",
            organizationName = "Jaffna IT Innovators Network",
            imageUrl = null,
            websiteUrl = "https://example.com/jaffna-tech-summit",
            contactPhone = "+94 21 221 4455",
            contactEmail = "summit@jaffnatech.lk",
            location = "Jaffna",
            category = "IT & Software",
            packageId = "pkg_standard",
            packageName = "Professional Banner",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 3,
            validUntil = System.currentTimeMillis() + 86400000 * 30
        ),
        Advertisement(
            id = "ad_008",
            title = "International English & IELTS Intensive Preparation",
            description = "Achieve Band 7.5+ with British Council certified instructors. Weekend and evening batches with comprehensive mock speaking exams.",
            organizationName = "Kandy Language Institute",
            imageUrl = null,
            websiteUrl = "https://example.com/kandy-ielts",
            contactPhone = "+94 81 220 3344",
            contactEmail = "admissions@kandyielts.lk",
            location = "Kandy",
            category = "Education & Teaching",
            packageId = "pkg_basic",
            packageName = "Starter Listing",
            status = AdStatus.APPROVED,
            createdAt = System.currentTimeMillis() - 86400000 * 2,
            validUntil = System.currentTimeMillis() + 86400000 * 28
        )
    )

    val sampleJobs = listOf(
        Job(
            id = "job_lk_001",
            title = "Senior Android Developer (Kotlin & Compose)",
            companyId = "comp_001",
            companyName = "Apex Digital Solutions Sri Lanka",
            companyLogoUrl = null,
            categoryId = "cat-it",
            categoryName = "IT & Software",
            location = "Colombo 03",
            district = "Colombo",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.SENIOR,
            workplaceType = WorkplaceType.HYBRID,
            salaryMin = 350000,
            salaryMax = 520000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = false,
            description = "We are seeking a seasoned Senior Android Engineer to spearhead our next-generation mobile banking and commerce applications. You will collaborate with cross-functional product squads across Colombo and Singapore, architecting clean, responsive Compose interfaces and robust offline-first Room architectures.",
            responsibilities = listOf(
                "Lead development of scalable native Android applications using Kotlin and Jetpack Compose",
                "Maintain Clean Architecture and unidirectional data flow (MVI / MVVM)",
                "Optimize memory usage, network efficiency, and startup times across low-end and flagship devices",
                "Mentor junior and mid-level software engineers and enforce modern code review standards"
            ),
            requirements = listOf(
                "5+ years of production Android development experience using Kotlin",
                "Deep understanding of Jetpack Compose, Coroutines, Flow, and Room",
                "Solid grasp of MVVM/MVI, repository pattern, and clean software architecture",
                "Bachelor's degree in Computer Science, Software Engineering, or equivalent practical experience"
            ),
            benefits = listOf(
                "Competitive salary pegged to USD with annual performance incentives",
                "Comprehensive family health insurance coverage",
                "Hybrid working arrangement (2 days remote, 3 days office in Colombo 03)",
                "Annual tech allowance and sponsored professional certifications"
            ),
            postedDate = "Today",
            closingDate = "Oct 28, 2026",
            isFeatured = true,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/senior-android-developer",
            applicationEmail = "careers@apexdigital.lk",
            source = "Direct Employer",
            viewsCount = 184
        ),
        Job(
            id = "job_lk_002",
            title = "Full-Stack Software Engineer (Java / React)",
            companyId = "comp_002",
            companyName = "Ceylon Cloud Innovations",
            companyLogoUrl = null,
            categoryId = "cat-it",
            categoryName = "IT & Software",
            location = "Colombo 07",
            district = "Colombo",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.MID_LEVEL,
            workplaceType = WorkplaceType.HYBRID,
            salaryMin = 220000,
            salaryMax = 340000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = true,
            description = "Join our enterprise cloud engineering group building microservices and dashboard interfaces for regional logistics and supply chain clients.",
            responsibilities = listOf(
                "Design and maintain microservices with Spring Boot and PostgreSQL",
                "Build modern, accessible frontend modules using React and TypeScript",
                "Write automated integration tests and manage CI/CD pipelines"
            ),
            requirements = listOf(
                "3+ years building web applications with Java (Spring Boot) and React",
                "Strong SQL and relational database design experience",
                "Familiarity with Docker and AWS container services"
            ),
            benefits = listOf(
                "OPD & hospitalization cover",
                "Flexible hours with weekly work-from-home options",
                "Annual learning & development stipend"
            ),
            postedDate = "1 day ago",
            closingDate = "Nov 02, 2026",
            isFeatured = false,
            isUrgent = true,
            applicationUrl = "https://example.com/careers/java-react-engineer",
            applicationEmail = "hr@ceyloncloud.lk",
            source = "Direct Employer",
            viewsCount = 210
        ),
        Job(
            id = "job_lk_003",
            title = "Senior Financial Accountant",
            companyId = "comp_003",
            companyName = "Lanka Commerce & Exports PLC",
            companyLogoUrl = null,
            categoryId = "cat-acc",
            categoryName = "Accounting & Finance",
            location = "Gampaha",
            district = "Gampaha",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.SENIOR,
            workplaceType = WorkplaceType.ON_SITE,
            salaryMin = 180000,
            salaryMax = 260000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = false,
            description = "Leading export conglomerate is looking for an experienced Chartered Accountant to oversee statutory reporting, tax compliance (VAT, SSCL, CIT), and group financial reconciliations.",
            responsibilities = listOf(
                "Prepare monthly financial statements in compliance with SLFRS/LKAS standards",
                "Handle corporate tax filings, RAMIS portal submissions, and audit queries",
                "Manage cost accounting for export shipments and foreign currency receivables",
                "Lead and supervise a junior accounting team of 4 associates"
            ),
            requirements = listOf(
                "Full membership in CA Sri Lanka, CIMA, or ACCA",
                "Minimum 4 years post-qualifying commercial experience in manufacturing or exports",
                "Proficiency in ERP systems (SAP or Oracle) and advanced Excel"
            ),
            benefits = listOf(
                "Performance-based annual bonus",
                "Company maintained transport allowance",
                "Executive medical plan"
            ),
            postedDate = "2 days ago",
            closingDate = "Oct 25, 2026",
            isFeatured = true,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/senior-accountant",
            applicationEmail = "recruitment@lankacommerce.lk",
            source = "Direct Employer",
            viewsCount = 142
        ),
        Job(
            id = "job_lk_004",
            title = "Digital Marketing & Growth Executive",
            companyId = "comp_004",
            companyName = "Blue Lotus Media",
            companyLogoUrl = null,
            categoryId = "cat-sales",
            categoryName = "Sales & Marketing",
            location = "Colombo 04",
            district = "Colombo",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.MID_LEVEL,
            workplaceType = WorkplaceType.HYBRID,
            salaryMin = 110000,
            salaryMax = 175000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = true,
            description = "Drive multi-channel acquisition campaigns for hospitality and consumer brands. You will oversee paid social media, Google Search Ads, email funnels, and conversion optimization.",
            responsibilities = listOf(
                "Execute performance marketing campaigns across Meta Ads, Google Ads, and TikTok",
                "Analyze conversion analytics, CPA, and ROAS on Google Analytics 4",
                "Collaborate with graphic designers and copywriters to produce high-converting creative assets"
            ),
            requirements = listOf(
                "2+ years running paid advertising campaigns with demonstrated ROI",
                "Certification in Google Ads or Meta Blueprint is an asset",
                "Strong analytical mindset and proficiency in Sinhala and English"
            ),
            benefits = listOf(
                "Quarterly performance incentives based on client campaign growth",
                "Casual office dress code and relaxed studio atmosphere",
                "Hybrid remote balance"
            ),
            postedDate = "3 days ago",
            closingDate = "Nov 10, 2026",
            isFeatured = false,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/digital-marketing",
            applicationEmail = "jobs@bluelotusmedia.lk",
            source = "Verified Partner",
            viewsCount = 95
        ),
        Job(
            id = "job_lk_005",
            title = "Electrical & Automation Project Engineer",
            companyId = "comp_005",
            companyName = "Southern Industrial Engineering",
            companyLogoUrl = null,
            categoryId = "cat-eng",
            categoryName = "Engineering",
            location = "Galle",
            district = "Galle",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.MID_LEVEL,
            workplaceType = WorkplaceType.ON_SITE,
            salaryMin = 140000,
            salaryMax = 210000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = false,
            description = "Manage electrical distribution, PLC automation, and preventive maintenance across large-scale food processing facilities in Galle and Matara.",
            responsibilities = listOf(
                "Oversee installation and commissioning of industrial electrical panels and motor control centers",
                "Troubleshoot Siemens and Schneider PLCs and SCADA networks",
                "Ensure plant compliance with CEB and national industrial safety protocols"
            ),
            requirements = listOf(
                "B.Sc. in Electrical / Mechatronics Engineering from a recognized university",
                "2+ years hands-on experience in manufacturing plant environments",
                "Valid driving license and willingness to commute between Galle and Matara sites"
            ),
            benefits = listOf(
                "Site allowances and travel reimbursement",
                "Group medical insurance",
                "Free on-site meals"
            ),
            postedDate = "4 days ago",
            closingDate = "Oct 30, 2026",
            isFeatured = false,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/electrical-engineer",
            applicationEmail = "careers@southernind.lk",
            source = "Direct Employer",
            viewsCount = 76
        ),
        Job(
            id = "job_lk_006",
            title = "Secondary English & Literature Teacher",
            companyId = "comp_006",
            companyName = "Hill Country International College",
            companyLogoUrl = null,
            categoryId = "cat-edu",
            categoryName = "Education & Teaching",
            location = "Kandy",
            district = "Kandy",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.MID_LEVEL,
            workplaceType = WorkplaceType.ON_SITE,
            salaryMin = 95000,
            salaryMax = 140000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = true,
            description = "Inspire secondary students preparing for Cambridge IGCSE and National O/L English curricula in a supportive, historic academic environment in Kandy.",
            responsibilities = listOf(
                "Deliver engaging literature and language lesson plans for Grades 8–11",
                "Assess coursework, conduct mock exams, and provide constructive student feedback",
                "Participate in co-curricular activities including Drama Club and Debating Society"
            ),
            requirements = listOf(
                "Bachelor of Arts in English Language / Literature or National Diploma in Teaching",
                "Proven track record of delivering successful Cambridge/Edexcel exam results",
                "Excellent classroom management and interpersonal communication skills"
            ),
            benefits = listOf(
                "Subsidized teacher accommodation in Kandy city outskirts",
                "Full school fees waiver for up to two children",
                "Paid school term vacations"
            ),
            postedDate = "5 days ago",
            closingDate = "Nov 05, 2026",
            isFeatured = false,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/english-teacher",
            applicationEmail = "principal@hillcountryintl.lk",
            source = "Direct Employer",
            viewsCount = 112
        ),
        Job(
            id = "job_lk_007",
            title = "Quality Assurance & Automation Engineer (Remote)",
            companyId = "comp_007",
            companyName = "Oceanic Fintech Labs",
            companyLogoUrl = null,
            categoryId = "cat-it",
            categoryName = "IT & Software",
            location = "Remote",
            district = "Remote",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.MID_LEVEL,
            workplaceType = WorkplaceType.REMOTE,
            salaryMin = 280000,
            salaryMax = 400000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = false,
            description = "Work 100% remotely from anywhere in Sri Lanka. Build automated regression test suites using Playwright, Appium, and REST Assured for our fintech platform.",
            responsibilities = listOf(
                "Develop and execute automated test scripts for web and mobile banking solutions",
                "Perform API performance and security testing using Postman and JMeter",
                "Collaborate with developers in sprint planning and bug triage meetings"
            ),
            requirements = listOf(
                "3+ years in automated software testing (TypeScript / Python / Java)",
                "Solid understanding of CI/CD integration using GitHub Actions",
                "Quiet home workspace with dependable fiber broadband"
            ),
            benefits = listOf(
                "100% remote flexibility with home office setup grant",
                "Flexible hours (core availability 10:00 AM - 4:00 PM IST)",
                "Dollar-pegged compensation structure"
            ),
            postedDate = "2 days ago",
            closingDate = "Nov 15, 2026",
            isFeatured = true,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/qa-remote",
            applicationEmail = "recruitment@oceanicfintech.com",
            source = "Direct Employer",
            viewsCount = 340
        ),
        Job(
            id = "job_lk_008",
            title = "Human Resources & Payroll Executive",
            companyId = "comp_008",
            companyName = "Wayamba Agri Exports",
            companyLogoUrl = null,
            categoryId = "cat-admin",
            categoryName = "Administration & HR",
            location = "Kurunegala",
            district = "Kurunegala",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.JUNIOR,
            workplaceType = WorkplaceType.ON_SITE,
            salaryMin = 75000,
            salaryMax = 110000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = false,
            description = "Oversee employee attendance, EPF/ETF submissions, recruitment administration, and factory welfare across our processing plants in Kurunegala.",
            responsibilities = listOf(
                "Process monthly payroll calculations, overtime, and statutory deductions",
                "Coordinate interviews, new joiner onboarding, and staff documentation",
                "Ensure compliance with Sri Lankan labor regulations and factory safety standards"
            ),
            requirements = listOf(
                "PQHRM (CIPM) or Diploma in Human Resource Management",
                "1-2 years relevant experience managing payroll and employee relations",
                "Good working knowledge of MS Excel and Sinhala/English typing"
            ),
            benefits = listOf(
                "Factory transport facilities from Kurunegala town",
                "Annual staff bonus and subsidized lunch",
                "Support for higher professional studies (CIPM)"
            ),
            postedDate = "3 days ago",
            closingDate = "Oct 29, 2026",
            isFeatured = false,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/hr-executive",
            applicationEmail = "careers@wayambaagri.lk",
            source = "Direct Employer",
            viewsCount = 88
        ),
        Job(
            id = "job_lk_009",
            title = "Software Engineering Intern (Android / Flutter)",
            companyId = "comp_001",
            companyName = "Apex Digital Solutions Sri Lanka",
            companyLogoUrl = null,
            categoryId = "cat-intern",
            categoryName = "Internships",
            location = "Colombo 03",
            district = "Colombo",
            employmentType = EmploymentType.INTERNSHIP,
            experienceLevel = ExperienceLevel.ENTRY_LEVEL,
            workplaceType = WorkplaceType.HYBRID,
            salaryMin = 45000,
            salaryMax = 65000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = false,
            description = "6-month paid internship for undergraduates or recent tech graduates passionate about mobile app development. You will receive direct 1-on-1 mentorship from senior software engineers.",
            responsibilities = listOf(
                "Assist in developing UI screens using Kotlin Jetpack Compose",
                "Participate in daily standups and sprint planning sessions",
                "Write unit tests and verify bug fixes"
            ),
            requirements = listOf(
                "Undergraduate in Computer Science, IT, or Software Engineering (3rd/4th year)",
                "Basic understanding of Kotlin or Flutter and Git version control",
                "Eagerness to learn and strong problem-solving initiative"
            ),
            benefits = listOf(
                "Monthly paid stipend of LKR 55,000",
                "Fast-track conversion to Associate Software Engineer upon completion",
                "Free lunch and snacks at the Colombo innovation lab"
            ),
            postedDate = "Yesterday",
            closingDate = "Nov 12, 2026",
            isFeatured = false,
            isUrgent = true,
            applicationUrl = "https://example.com/careers/intern-software",
            applicationEmail = "internships@apexdigital.lk",
            source = "Direct Employer",
            viewsCount = 420
        ),
        Job(
            id = "job_lk_010",
            title = "Corporate Sales & Account Manager",
            companyId = "comp_009",
            companyName = "Serendib Telecom Solutions",
            companyLogoUrl = null,
            categoryId = "cat-sales",
            categoryName = "Sales & Marketing",
            location = "Negombo",
            district = "Gampaha",
            employmentType = EmploymentType.FULL_TIME,
            experienceLevel = ExperienceLevel.MID_LEVEL,
            workplaceType = WorkplaceType.ON_SITE,
            salaryMin = 120000,
            salaryMax = 190000,
            salaryCurrency = "LKR",
            isSalaryNegotiable = true,
            description = "Manage business client accounts for enterprise connectivity, cloud PBX, and corporate data packages across Western and North Western provinces.",
            responsibilities = listOf(
                "Build and manage corporate sales pipelines through direct client meetings and presentations",
                "Negotiate contracts and achieve monthly enterprise sales quotas",
                "Maintain strong ongoing relationships with key corporate decision-makers"
            ),
            requirements = listOf(
                "3+ years experience in B2B corporate sales or telecommunications solutions",
                "Excellent presentation and negotiation skills in English and Sinhala",
                "Possession of a car or motorcycle with valid license"
            ),
            benefits = listOf(
                "Uncapped sales commission and fuel allowance",
                "Corporate mobile phone and unlimited voice/data plan",
                "Medical health insurance"
            ),
            postedDate = "4 days ago",
            closingDate = "Nov 04, 2026",
            isFeatured = false,
            isUrgent = false,
            applicationUrl = "https://example.com/careers/sales-manager",
            applicationEmail = "careers@serendibtelecom.lk",
            source = "Direct Employer",
            viewsCount = 105
        )
    )
}

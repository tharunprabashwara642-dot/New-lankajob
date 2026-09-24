package com.example

import com.example.domain.model.EmploymentType
import com.example.domain.model.ExperienceLevel
import com.example.domain.model.Job
import com.example.domain.model.JobFilter
import com.example.domain.model.JobSortOrder
import com.example.domain.model.WorkplaceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class JobFilteringAndSortingTest {

    private lateinit var testJobs: List<Job>

    @Before
    fun setup() {
        testJobs = listOf(
            Job(
                id = "job-1",
                title = "Senior Android Developer",
                companyId = "comp-1",
                companyName = "TechCorp Lanka",
                categoryId = "cat-it",
                categoryName = "IT & Software",
                location = "Colombo",
                district = "Colombo",
                employmentType = EmploymentType.FULL_TIME,
                experienceLevel = ExperienceLevel.SENIOR,
                workplaceType = WorkplaceType.HYBRID,
                salaryMin = 300000,
                salaryMax = 500000,
                description = "Build world class apps",
                postedDate = "Today",
                isFeatured = true
            ),
            Job(
                id = "job-2",
                title = "Junior Accountant",
                companyId = "comp-2",
                companyName = "Finance Lanka",
                categoryId = "cat-acc",
                categoryName = "Accounting & Finance",
                location = "Kandy",
                district = "Kandy",
                employmentType = EmploymentType.FULL_TIME,
                experienceLevel = ExperienceLevel.JUNIOR,
                workplaceType = WorkplaceType.ON_SITE,
                salaryMin = 80000,
                salaryMax = 120000,
                description = "Handle day to day book keeping",
                postedDate = "Yesterday",
                isFeatured = false
            ),
            Job(
                id = "job-3",
                title = "Remote Content Writer",
                companyId = "comp-3",
                companyName = "Media Works",
                categoryId = "cat-sales",
                categoryName = "Sales & Marketing",
                location = "Remote",
                district = "Remote",
                employmentType = EmploymentType.PART_TIME,
                experienceLevel = ExperienceLevel.MID_LEVEL,
                workplaceType = WorkplaceType.REMOTE,
                salaryMin = 150000,
                salaryMax = 200000,
                description = "Write engaging digital content",
                postedDate = "3 days ago",
                isFeatured = false
            )
        )
    }

    @Test
    fun filterByCategory_returnsMatchingJobs() {
        val filter = JobFilter(categoryId = "cat-it")
        val filtered = testJobs.filter { it.categoryId == filter.categoryId }
        assertEquals(1, filtered.size)
        assertEquals("Senior Android Developer", filtered[0].title)
    }

    @Test
    fun filterByEmploymentType_returnsMatchingJobs() {
        val filter = JobFilter(employmentType = EmploymentType.PART_TIME)
        val filtered = testJobs.filter { it.employmentType == filter.employmentType }
        assertEquals(1, filtered.size)
        assertEquals("Remote Content Writer", filtered[0].title)
    }

    @Test
    fun filterByWorkplaceType_returnsRemoteJobs() {
        val filter = JobFilter(workplaceType = WorkplaceType.REMOTE)
        val filtered = testJobs.filter { it.workplaceType == filter.workplaceType }
        assertEquals(1, filtered.size)
        assertEquals(WorkplaceType.REMOTE, filtered[0].workplaceType)
    }

    @Test
    fun filterBySalary_returnsAboveMinimum() {
        val minSalary = 200000
        val filtered = testJobs.filter { (it.salaryMin ?: 0) >= minSalary }
        assertEquals(1, filtered.size)
        assertEquals("Senior Android Developer", filtered[0].title)
    }

    @Test
    fun filterByFeatured_returnsOnlyFeaturedJobs() {
        val filtered = testJobs.filter { it.isFeatured }
        assertEquals(1, filtered.size)
        assertTrue(filtered[0].isFeatured)
    }

    @Test
    fun sortBySalaryHighToLow_ordersCorrectly() {
        val sorted = testJobs.sortedByDescending { it.salaryMax ?: it.salaryMin ?: 0 }
        assertEquals("job-1", sorted[0].id) // 500,000
        assertEquals("job-3", sorted[1].id) // 200,000
        assertEquals("job-2", sorted[2].id) // 120,000
    }
}

package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        // Given a list with one active task (not completed)
        val tasks = listOf(
            Task("title", "desc", isCompleted = false)
        )

        // When the stats are calculated
        val result = getActiveAndCompletedStats(tasks)

        // Then active tasks should be 100% and completed tasks should be 0%
        assertThat(result.activeTasksPercent, `is`(100f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnsZeroHundred() {
        // Given a list with one completed task
        val tasks = listOf(
            Task("title", "desc", isCompleted = true)
        )

        // When the stats are calculated
        val result = getActiveAndCompletedStats(tasks)

        // Then active tasks should be 0% and completed tasks should be 100%
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_both_returnsFortySixty() {
        // Given a list with 3 completed tasks and 2 active tasks
        val tasks = listOf(
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false)
        )

        // When the stats are calculated
        val result = getActiveAndCompletedStats(tasks)

        // Then the result should be 40% active and 60% completed
        assertThat(result.activeTasksPercent, `is`(40f))
        assertThat(result.completedTasksPercent, `is`(60f))
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        // When the function is given a null list (error case)
        val result = getActiveAndCompletedStats(null)

        // Then both active and completed tasks percentages should be 0%
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeros() {
        // When given an empty list
        val result = getActiveAndCompletedStats(emptyList())

        // Then both active and completed tasks percentages should be 0%
        assertThat(result.activeTasksPercent, `is`(0f))
        assertThat(result.completedTasksPercent, `is`(0f))
    }
}

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFilterType
import com.example.android.architecture.blueprints.todoapp.tasks.TasksViewModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [30]) // Consider updating if Robolectric supports SDK 31+
@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    // Ensures tasks execute synchronously, avoiding threading issues in tests.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // ViewModel is instantiated directly using ApplicationProvider,
        // which may make testing harder if dependencies change.
        // Consider using dependency injection with a fake repository.
        tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {
        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event should be triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        // Ensure the event is not null (verifying that it was triggered)
        assertThat(value?.getContentIfNotHandled(), (not(nullValue())))
    }

    @Test
    fun getTasksAddViewVisible() {
        // When the filter type is set to ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" button should be visible
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
    }
}

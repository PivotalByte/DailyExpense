package com.dailyexpense.presentation

import com.dailyexpense.data.models.TagWithCount
import com.dailyexpense.data.room.entity.TagEntity
import com.dailyexpense.fakes.FakeTagRepository
import com.dailyexpense.fakes.familyTag
import com.dailyexpense.presentation.TagViewModel.EditorError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TagViewModelTest {

    private val trip = TagEntity(tagId = 10, name = "Trip")
    private val repository = FakeTagRepository(listOf(familyTag, trip))
    private lateinit var vm: TagViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        vm = TagViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `renaming keeps the tag id`() {
        vm.startEdit(TagWithCount(familyTag, transactionCount = 3))
        vm.updateName("Household")
        vm.save()

        assertEquals(TagEntity(tagId = familyTag.tagId, name = "Household"), repository.saved.single())
        assertNull(vm.editor.value)
    }

    @Test
    fun `renaming to another tag's name is rejected, case-insensitively`() {
        vm.startEdit(TagWithCount(familyTag, transactionCount = 3))
        vm.updateName("trip")
        vm.save()

        assertEquals(EditorError.DuplicateName, vm.editor.value?.error)
        assertTrue(repository.saved.isEmpty())
    }

    @Test
    fun `new tag needs a name`() {
        vm.startNew()
        vm.updateName("   ")
        vm.save()

        assertEquals(EditorError.EmptyName, vm.editor.value?.error)
    }

    @Test
    fun `delete removes the tag`() {
        vm.startEdit(TagWithCount(trip, transactionCount = 1))
        vm.delete()

        assertEquals(trip, repository.deleted.single())
        assertNull(vm.editor.value)
    }
}

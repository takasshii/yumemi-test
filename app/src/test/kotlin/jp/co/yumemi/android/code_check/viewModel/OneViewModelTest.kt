package jp.co.yumemi.android.code_check.viewModel

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class OneViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var viewModel: OneViewModel

    @Before
    fun init() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testSearchResults() {
        //Kotlinの検索結果がでてくるか
        val inputText: String = "Kotlin"
        viewModel.searchResults(inputText)
        assertEquals(inputText,viewModel.items.value?.get(0)?.name)
    }

    @Test
    fun testSearchResultsError() {
        //空白の際にエラーが返されるか
        val inputText: String = ""
        viewModel.searchResults(inputText)
        assertEquals(viewModel.errorContent.value, "エラーが発生しました。")
    }
}
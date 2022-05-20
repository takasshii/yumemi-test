package jp.co.yumemi.android.code_check.viewModel

import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jp.co.yumemi.android.code_check.domain.model.api.IApiRepository
import jp.co.yumemi.android.code_check.domain.model.getResources.IGetResources
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
class OneViewModelTest {

    private lateinit var viewModel: OneViewModel

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var getResourcesRepository: IGetResources

    @Inject
    lateinit var apiRepository: IApiRepository

    @Before
    fun init() {
        hiltRule.inject()
        viewModel = OneViewModel(getResourcesRepository,apiRepository)
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
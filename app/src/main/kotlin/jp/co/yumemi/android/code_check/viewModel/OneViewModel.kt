/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewModel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.call.*
import jp.co.yumemi.android.code_check.domain.model.api.IApiRepository
import jp.co.yumemi.android.code_check.domain.model.getResources.IGetResources
import jp.co.yumemi.android.code_check.domain.model.item.Item
import jp.co.yumemi.android.code_check.view.activity.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/**
 * TwoFragment で使う
 */
@HiltViewModel
class OneViewModel @Inject constructor(
    private val getResourcesRepository: IGetResources,
    private val apiRepository: IApiRepository
) : ViewModel() {

    //入力欄の保持用
    private val _searchInputText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val searchInputText: LiveData<String>
        get() = _searchInputText

    //検索結果の表示用
    private val _items: MutableLiveData<List<Item>> by lazy {
        MutableLiveData<List<Item>>()
    }
    val items: LiveData<List<Item>>
        get() = _items

    //エラー内容の表示用
    private val _errorContent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val errorContent: LiveData<String>
        get() = _errorContent


    // 検索結果を表示するために呼ばれる関数
    fun searchResults(inputText: String) {
        //一時格納用
        val tempItems = mutableListOf<Item>()

        //非同期処理で実行
        viewModelScope.launch {
            //apiRepositoryからデータが送られてきたら走る処理
            apiRepository.getHttpResponse(inputText).catch { exception ->
                //エラー処理
                notifyError(exception)
                //空白のItemを返す
                _items.value = tempItems.toList()
            }.map {
                //受け取ったデータをJSON型に加工
                JSONObject(it.receive<String>()).optJSONArray("items")
            }.onEach {
                //データがなかった時は何も行わない
                if(it != null) {
                    //データが存在する時の処理
                    //jsonのパース処理
                    for (i in 0 until it.length()) {
                        //nullだった場合はskipして次の処理に移る
                        val jsonItem = it.optJSONObject(i) ?: continue

                        //以下のパース処理でnullだった場合は「空白か0」が格納される
                        val name = jsonItem.optString("full_name")
                        val ownerIconUrl = jsonItem.optJSONObject("owner")?.optString("avatar_url") ?: ""
                        val language = jsonItem.optString("language")
                        val stargazersCount = jsonItem.optLong("stargazers_count")
                        val watchersCount = jsonItem.optLong("watchers_count")
                        val forksCount = jsonItem.optLong("forks_count")
                        val openIssuesCount = jsonItem.optLong("open_issues_count")

                        tempItems.add(
                            Item(
                                name = name,
                                ownerIconUrl = ownerIconUrl,
                                language = getResourcesRepository.getStringResources(language),
                                stargazersCount = stargazersCount,
                                watchersCount = watchersCount,
                                forksCount = forksCount,
                                openIssuesCount = openIssuesCount
                            )
                        )
                    }
                    lastSearchDate = Date()
                    //LIVEDataを更新
                    _items.value = tempItems.toList()
                }
            }.launchIn(viewModelScope)
        }
    }

    //エラー表示用
    private fun notifyError(exception: Throwable) {
        _errorContent.value = "エラーが発生しました。検索し直してください。\n エラー内容: $exception"
    }
}


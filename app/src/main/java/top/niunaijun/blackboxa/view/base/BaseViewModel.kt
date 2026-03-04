package top.niunaijun.blackboxa.view.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val loadingState = MutableLiveData<Boolean>()
    val errorState = MutableLiveData<String>()

    protected fun setLoading(loading: Boolean) { loadingState.postValue(loading) }
    protected fun setError(msg: String) { errorState.postValue(msg) }
}

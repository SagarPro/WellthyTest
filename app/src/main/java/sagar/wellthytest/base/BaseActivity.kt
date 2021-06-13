package sagar.wellthytest.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity<VM : ViewModel> : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: VM

    @LayoutRes
    protected abstract fun provideLayout(): Int

    protected abstract fun provideViewModelClass(): Class<VM>

    protected abstract fun initViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayout())
        initViews()
        viewModel = ViewModelProvider(this, viewModelFactory).get(provideViewModelClass())
    }

    protected fun getViewModel(): VM {
        return viewModel
    }
}
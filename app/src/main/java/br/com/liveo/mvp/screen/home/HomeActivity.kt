package br.com.liveo.mvp.screen.home

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import br.com.liveo.mvp.App
import br.com.liveo.mvp.R
import br.com.liveo.mvp.base.BaseActivity
import br.com.liveo.mvp.databinding.ActivityHomeBinding
import br.com.liveo.mvp.di.scope.ActivityScoped
import br.com.liveo.mvp.extension.toastShort
import br.com.liveo.mvp.model.domain.UserResponse
import br.com.liveo.mvp.screen.home.di.HomeModule
import javax.inject.Inject

/**
 * Created by rudsonlima on 8/29/17.
 */
@ActivityScoped
class HomeActivity : BaseActivity(), HomeContract.View {

    private var mBinding: ActivityHomeBinding? = null

    @Inject
    lateinit var mHomePresenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.onInitInject()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        this.fetchUsers()
    }

    override fun onInitInject() {
        App.application.module(HomeModule()).inject(this)

        this.onInitView()
    }

    override fun onInitView() {
        mBinding = this.bindView(R.layout.activity_home) as ActivityHomeBinding

        this.toolbar(mBinding?.includeToolbar?.toolbar).
                icon(R.drawable.ic_arrow_back).
                title(R.string.app_name).
                builder()

        mBinding?.recyclerView?.layoutManager = LinearLayoutManager(this)

        mBinding?.swipeContainer?.setOnRefreshListener(onRefresh)
        mBinding?.swipeContainer?.setColorSchemeResources(R.color.accent, R.color.accent,
                R.color.accent, R.color.accent)
    }

    private val onRefresh = SwipeRefreshLayout.OnRefreshListener { fetchUsers() }

    private fun fetchUsers() {
        this.mHomePresenter.attach(this)
        this.mHomePresenter.fetchUsers()
    }

    override fun onLoading(loading: Boolean) {
        mBinding?.swipeContainer?.isRefreshing = loading
    }

    override fun onError(error: Throwable?) {}

    override val page: Int
        get() = 2

    override fun onUserResponse(userResponse: UserResponse) {
        val adapter = HomeAdapter(userResponse)

        adapter.onItemClick = { _, _, user ->
            this.toastShort(user.name)
        }

        mBinding?.recyclerView?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        mHomePresenter.detachView()
    }

    override fun finishActivity() {
        finish()
    }
}

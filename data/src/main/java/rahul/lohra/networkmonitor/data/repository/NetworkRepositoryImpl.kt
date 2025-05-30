package rahul.lohra.snorkl.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import rahul.lohra.snorkl.data.local.dao.NetworkLogDao
import rahul.lohra.snorkl.data.local.entities.NetworkEntity

class NetworkRepositoryImpl(
    private val dao: NetworkLogDao
) : NetworkRepository {

    override fun getPagedNetworkLogs(): Pager<Int, NetworkEntity> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { dao.getPaged() }
        )
    }

    override suspend fun clearAll() {
        dao.clear()
    }

    override suspend fun getAllLogs(): List<NetworkEntity> {
        return dao.getAll()
    }
}

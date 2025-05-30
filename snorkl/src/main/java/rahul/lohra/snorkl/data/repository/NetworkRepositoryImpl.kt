package rahul.lohra.snorkl.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
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

    override suspend fun delete(id: String) {
        dao.delete(id)
    }

    override suspend fun getAllLogs(): List<NetworkEntity> {
        return dao.getAll()
    }

    override suspend fun getLog(id: String): Flow<NetworkEntity> {
        return dao.getNetworkLog(id)
    }
}

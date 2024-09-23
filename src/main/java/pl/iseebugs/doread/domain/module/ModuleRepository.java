package pl.iseebugs.doread.domain.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ModuleRepository extends JpaRepository<Module, Long> {
}

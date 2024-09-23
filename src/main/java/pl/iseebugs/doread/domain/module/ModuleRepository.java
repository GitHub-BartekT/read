package pl.iseebugs.doread.domain.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findAllByUserId(Long userId);
    List<Module> findAllByIsPrivateFalse();
    Optional<Module> findByIdAndUserId(Long id, Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
}

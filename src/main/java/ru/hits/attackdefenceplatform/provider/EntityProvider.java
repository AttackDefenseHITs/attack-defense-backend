package ru.hits.attackdefenceplatform.provider;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Базовый провайдер доменных сущностей.
 * Унифицирует операции поиска, проверки, сохранения и удаления сущностей.
 */
public abstract class EntityProvider<T> {

    /**
     * Возвращает Optional-сущность по ID.
     */
    protected abstract Optional<T> findById(UUID id);

    /**
     * Возвращает имя сущности (для сообщений об ошибках).
     */
    protected abstract String entityName();

    /**
     * Возвращает связанный репозиторий JPA.
     */
    protected abstract JpaRepository<T, UUID> getRepository();

    /**
     * Получает сущность по ID или выбрасывает исключение, если не найдена.
     */
    public T getById(UUID id) {
        return findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(entityName() + " с ID " + id + " не найден"));
    }

    /**
     * Проверяет существование сущности.
     * Если не существует — выбрасывает исключение.
     */
    public boolean exists(UUID id) {
        boolean present = findById(id).isPresent();
        if (!present) {
            throw new EntityNotFoundException(entityName() + " с ID " + id + " не найден");
        }
        return true;
    }

    /**
     * Ищет сущность без выброса исключения.
     */
    public Optional<T> findOptional(UUID id) {
        return findById(id);
    }

    /**
     * Сохраняет сущность в базе.
     */
    public T save(T entity) {
        return getRepository().save(entity);
    }

    /**
     * Удаляет сущность по ID.
     * Если не найдена — выбрасывает исключение.
     */
    public void delete(UUID id) {
        var entity = getById(id);
        getRepository().delete(entity);
    }

    /**
     * Удаляет сущность напрямую.
     */
    public void delete(T entity) {
        getRepository().delete(entity);
    }
}

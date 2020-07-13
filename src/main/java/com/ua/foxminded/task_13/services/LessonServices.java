package com.ua.foxminded.task_13.services;


import com.ua.foxminded.task_13.dao.impl.LectorDaoImplEntity;
import com.ua.foxminded.task_13.dao.impl.LessonDaoImpl;
import com.ua.foxminded.task_13.dto.LessonDto;
import com.ua.foxminded.task_13.exceptions.ServiceException;
import com.ua.foxminded.task_13.model.Lector;
import com.ua.foxminded.task_13.model.Lesson;
import com.ua.foxminded.task_13.validation.ValidatorEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.ejb.NoSuchEntityException;
import java.util.ArrayList;
import java.util.List;


@Service
public class LessonServices {

    @Autowired
    private LessonDaoImpl lessonDao;
    @Autowired
    private LectorDaoImplEntity lectorDao;
    @Autowired
    private ValidatorEntity<Lesson> validator;

    private static final Logger logger = LoggerFactory.getLogger(LessonServices.class);

    private static final String MISSING_ID = "Missing id lesson.";
    private static final String NOT_EXIST_ENTITY = "Doesn't exist such lesson";


    private LessonDto getDtoById(Long id) {

        Lesson lesson = lessonDao.getById(id);
        Lector lector = lectorDao.getById(lesson.getLectorId());

        LessonDto lessonDto = new LessonDto();
        lessonDto.setLessonId(lesson.getLessonId());
        lessonDto.setName(lesson.getName());
        lessonDto.setLector(lector);

        return lessonDto;
    }

    private List<LessonDto> getAllDto() {
        List<Lesson> lessons = lessonDao.getAll();
        List<LessonDto> lessonDtos = new ArrayList<>();

        Lector lector;
        LessonDto lessonDto;

        for (Lesson lesson : lessons) {

            lector = lectorDao.getById(lesson.getLectorId());

            lessonDto = new LessonDto();
            lessonDto.setLessonId(lesson.getLessonId());
            lessonDto.setName(lesson.getName());
            lessonDto.setLector(lector);

            lessonDtos.add(lessonDto);
        }

        return lessonDtos;
    }

    public LessonDto getById(long id) {
        logger.debug("Trying to get lesson with id={}", id);

        if (id == 0) {
            logger.warn(MISSING_ID);
            throw new ServiceException(MISSING_ID);
        }
        LessonDto lesson;
        try {
            lesson = getDtoById(id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Not existing lesson with id={}", id);
            throw new NoSuchEntityException(NOT_EXIST_ENTITY);
        } catch (DataAccessException e) {
            logger.error("failed to retrieve lesson with id={}", id, e);
            throw new ServiceException("Failed to retrieve lesson by id", e);
        }
        return lesson;
    }
    public List<LessonDto> getAll() {
        logger.debug("Trying to get all lessons");

        try {
            return getAllDto();
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Lessons is not exist");
            throw new NoSuchEntityException("Doesn't exist such lessons");
        } catch (DataAccessException e) {
            logger.error("Failed to get all lessons", e);
            throw new ServiceException("Failed to get list of lessons", e);
        }
    }

    public Lesson getByIdLight(long id) {
        logger.debug("Trying to get lesson with id={}", id);

        if (id == 0) {
            logger.warn(MISSING_ID);
            throw new ServiceException(MISSING_ID);
        }
        Lesson lesson;
        try {
            lesson = lessonDao.getById(id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Not existing lesson with id={}", id);
            throw new NoSuchEntityException(NOT_EXIST_ENTITY);
        } catch (DataAccessException e) {
            logger.error("failed to retrieve lesson with id={}", id, e);
            throw new ServiceException("Failed to retrieve lesson by id", e);
        }
        return lesson;
    }

    public List<Lesson> getAllLight() {
        logger.debug("Trying to get all lessons");

        try {
            return lessonDao.getAll();
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Lessons is not exist");
            throw new NoSuchEntityException("Doesn't exist such lessons");
        } catch (DataAccessException e) {
            logger.error("Failed to get all lessons", e);
            throw new ServiceException("Failed to get list of lessons", e);
        }
    }


    public boolean create(Lesson lesson) {
        logger.debug("Trying to create lesson: {}", lesson);

        validator.validate(lesson);
        try {
            return lessonDao.create(lesson);
        } catch (DataAccessException e) {
            logger.error("Failed to create lesson: {}", lesson, e);
            throw new ServiceException("Failed to create lesson", e);
        }
    }

    public boolean delete(long id) {
        logger.debug("Trying to delete lesson with id={}", id);

        if (id == 0) {
            logger.warn(MISSING_ID);
            throw new ServiceException(MISSING_ID);
        }

        try {
            return lessonDao.delete(id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Not existing lesson with id={}", id);
            throw new NoSuchEntityException(NOT_EXIST_ENTITY);
        } catch (DataAccessException e) {
            logger.error("Failed to delete lesson with id={}", id, e);
            throw new ServiceException("Failed to delete lesson by id", e);
        }
    }

    public boolean update(Lesson lesson) {
        logger.debug("Trying to update lesson: {}", lesson);

        if (lesson.getLessonId() == 0) {
            logger.warn(MISSING_ID);
            throw new ServiceException(MISSING_ID);
        }
        validator.validate(lesson);
        try {
            lessonDao.getById(lesson.getLessonId());
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Not existing lesson: {}", lesson);
            throw new NoSuchEntityException(NOT_EXIST_ENTITY);
        } catch (DataAccessException e) {
            logger.error("failed to retrieve lesson: {}", lesson, e);
            throw new ServiceException("Failed to retrieve lesson by id", e);
        }
        try {
            return lessonDao.update(lesson);
        } catch (DataAccessException e) {
            logger.error("failed to update lesson: {}", lesson, e);
            throw new ServiceException("Problem with updating lesson");
        }
    }
}

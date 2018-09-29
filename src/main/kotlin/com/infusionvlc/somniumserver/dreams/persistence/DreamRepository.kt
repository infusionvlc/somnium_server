package com.infusionvlc.somniumserver.dreams.persistence

import org.springframework.data.repository.PagingAndSortingRepository

interface DreamRepository : PagingAndSortingRepository<DreamEntity, Long>

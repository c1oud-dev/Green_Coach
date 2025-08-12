package com.greencoach.controller

import com.greencoach.model.CategoryDto
import com.greencoach.service.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryController (
    private val categoryService: CategoryService
) {
    /** GET  /api/categories */
    @GetMapping
    fun fetchTopCategories(): List<CategoryDto> =
        categoryService.getTopCategories()

    /** GET  /api/categories/{name}/sub */
    @GetMapping("/{name}/sub")
    fun fetchSubCategories(
        @PathVariable("name") name: String
    ): List<CategoryDto> =
        categoryService.getSubCategories(name)
}
package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.services.CategoryService;
import com.fullStack.expenseTracker.services.TransactionTypeService;
import com.fullStack.expenseTracker.dto.requests.CategoryRequestDto;
import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.exceptions.CategoryAlreadyExistsException;
import com.fullStack.expenseTracker.exceptions.CategoryNotFoundException;
import com.fullStack.expenseTracker.exceptions.CategoryServiceLogicException;
import com.fullStack.expenseTracker.exceptions.TransactionTypeNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final TransactionTypeService transactionTypeService;

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> getAllCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/getByUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getCategoriesByUser(@Param("email") String email) 
            throws UserNotFoundException {
        return categoryService.getCategoriesByUser(email);
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> addNewCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto)
            throws CategoryServiceLogicException, TransactionTypeNotFoundException, CategoryAlreadyExistsException, UserNotFoundException {
        return categoryService.addNewCategory(categoryRequestDto);
    }

    @PostMapping("/user/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> addUserCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto)
            throws CategoryServiceLogicException, TransactionTypeNotFoundException, CategoryAlreadyExistsException, UserNotFoundException {
        return categoryService.addUserCategory(categoryRequestDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> updateCategory(@Param("categoryId") int categoryId,
                                                            @RequestBody @Valid CategoryRequestDto categoryRequestDto)
            throws CategoryServiceLogicException, CategoryNotFoundException, TransactionTypeNotFoundException, UserNotFoundException {
        return categoryService.updateCategory(categoryId, categoryRequestDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> disableOrEnableCategory(@Param("categoryId") int categoryId)
            throws CategoryServiceLogicException, CategoryNotFoundException {
        return categoryService.enableOrDisableCategory(categoryId);
    }

}

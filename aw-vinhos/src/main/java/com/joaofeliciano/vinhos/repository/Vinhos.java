package com.joaofeliciano.vinhos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joaofeliciano.vinhos.model.Vinho;

public interface Vinhos extends JpaRepository<Vinho, Long> {

}

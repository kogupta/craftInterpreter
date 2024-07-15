package org.kogu.lox.ch7_eval;

import org.kogu.lox.ch4_scanning.Token;
import org.kogu.lox.ch5_ast.BinaryOperator;

public record BinaryToken(BinaryOperator op, Token token) {}


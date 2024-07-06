### Crafting interpreters

Working through the delightful [book](https://craftinginterpreters.com/contents.html).

#### Chapter 4

- use `switch` statements with [disjoint branches](https://horstmann.com/unblog/2020-02-04/index.html) to break free 
  from myriad `break`s
  ```java
    case '(' -> addToken(LEFT_PAREN);
    case ')' -> addToken(RIGHT_PAREN);
    // ...
    case ' ', '\r', '\t' -> {}       // whitespace
    case '\n' -> line++;             // line ending
    // ...
    default -> { ... }
  ```

- logically, scanner can be thought of as `Source => Stream[Token]`

  ![Lexical Analygator](misc/lexicalAnalygator.png)

- "contiguous" errors can be batched as follows:
  - model errors as: `record ScanError(int line, int start, int end, String message)`
  - accumulate errors in JDK ArrayList
  - merge current error with last error if they are consecutive characters
  - since errors are appended to this list, insert/delete of last error is very cheap (an array access)


#### Chapter 5

- `sealed` traits and pattern matching y'all
- breakfast grammar: 
  ```text
  breakfast  -> protein "with" breakfast "on the side" ;
  breakfast  -> protein ;                => protein ("with" breakfast "on the side")? | bread
  breakfast  -> bread ;
             
  protein    -> crispiness "crispy" "bacon" ;
  protein    -> "sausage" ;
  protein    -> cooked "eggs" ;          => ("scrambled" | "poached" | "fried" ) "eggs" ;
             
  crispiness -> "really" ;
  crispiness -> "really" crispiness ;    =>  crispiness -> "really"+ ;
             
  cooked     -> "scrambled" ;
  cooked     -> "poached" ;              => cooked -> "scrambled" | "poached" | "fried" ;
  cooked     -> "fried" ;
             
  bread      -> "toast" ;
  bread      -> "biscuits" ;             => bread -> "toast" | "biscuits" | "English muffin";
  bread      -> "English muffin" ;
  ```

  in regex terms become: 
  ```text
  breakfast -> protein ( "with" breakfast "on the side" )? | bread ;
  protein   -> "really"+ "crispy" "bacon" | "sausage" | ( "scrambled" | "poached" | "fried" ) "eggs" ;
  bread     -> "toast" | "biscuits" | "English muffin" ;
  ```
- lox init grammar: 
  ```text
  expression  ->  literal
               | unary
               | binary
               | grouping ;
  literal     -> NUMBER | STRING | "true" | "false" | "nil" ;
  grouping    -> "(" expression ")" ;
  unary       -> ( "-" | "!" ) expression ;
  binary      -> expression operator expression ;
  operator    -> "==" | "!=" | "<" | "<=" | ">" | ">=" | "+"  | "-"  | "*" | "/" ;
  ```

- operators: 

  | symbol | repetition                |
  |--------|---------------------------|
  | `*`    | 0 or more                 |
  | `+`    | at least once - 1 or more |
  | `?`    | at most once - 0 or 1     |
  

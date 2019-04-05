Ruby on Rails Cheat Sheet
=

# Tests

## Display SQL queries while running tests

The SQL queries logged in the `development` mode are not displayed on the output. They are however
logged in file `log/test.log`. To display them:

```bash
tail -f log/test.log
```

package ru.netology.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Handler {

    void Handle(HttpServletRequest req, HttpServletResponse resp, Long param) throws IOException;

}

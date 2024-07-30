package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPageV1;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

public class MoneyTransferTest {
  DashboardPage dashboardPage;
  CardInfo firstCardInfo;
  CardInfo secondCardInfo;
  int firstCardBalance;
  int secondCardBalance;

  @BeforeEach
  void setup() {
    var loginPage = open("http://localhost:9999", LoginPageV1.class);
    var authInfo = getAuthInfo();
    var verificationPage = loginPage.validLogin((authInfo));
    var verificationCode = getVerificationCode();
    verificationPage.validVerify(verificationCode);
    firstCardInfo = getFirstCardInfo();
    secondCardInfo = getSecondCardInfo();
    firstCardBalance = dashboardPage.getCardBalance(String.valueOf(firstCardInfo));
    secondCardBalance = dashboardPage.getCardBalance(String.valueOf(secondCardInfo));
  }

  @Test
  void shouldTransferFromFirstToSecond() {
    var amount = generateValidAmount(firstCardBalance);
    var expectedBalanceFirstCard = firstCardBalance - amount;
    var expectedBalanceSecondCard = secondCardBalance + amount;
    var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
    dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
    var actualBalanceFirstCard = dashboardPage.getCardBalance(String.valueOf(firstCardInfo));
    var actualBalanceSecondCard = dashboardPage.getCardBalance(String.valueOf(secondCardInfo));
    assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
    assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
  }

  @Test
  void shouldGetErrorMessageIfAmountMoreBalance() {
    var amount = generateInvalidAmount(secondCardBalance);
    var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
    transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
    transferPage.findErrorMessage("Выполнена попытка перевода суммы,превыщающией остаток на карте списания");
    var actualBalanceFirstCard = dashboardPage.getCardBalance(String.valueOf(firstCardInfo));
    var actualBalanceSecondCard = dashboardPage.getCardBalance(String.valueOf(secondCardInfo));
    assertEquals(firstCardBalance, actualBalanceFirstCard);
    assertEquals(secondCardBalance, actualBalanceSecondCard);
  }

}
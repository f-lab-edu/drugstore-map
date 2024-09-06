package org.healthmap.openapi.api;

import org.healthmap.openapi.dto.FacilityDetailDto;
import org.healthmap.openapi.utility.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacilityDetailInfoApi {
    public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException {
        String url = "http://apis.data.go.kr/B551182/MadmDtlInfoService2.7/getDtlInfo2.7"
                + "?serviceKey=" + "서비스키"    //Service Key
                + "&ykiho=암호요양기호";

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document parse = documentBuilder.parse(url);

        parse.getDocumentElement().normalize();
        NodeList nodeList = parse.getElementsByTagName("item");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) item;
                String parking = XmlUtils.getStringFromElement("parkXpnsYn", element);
                String parkingEtc = XmlUtils.getStringFromElement("parkEtc", element);
                String treatmentMon = XmlUtils.getTreatmentTimeFromElement("trmtMonStart", "trmtMonEnd", element);
                String treatmentTue = XmlUtils.getTreatmentTimeFromElement("trmtTueStart", "trmtTueEnd", element);
                String treatmentWed = XmlUtils.getTreatmentTimeFromElement("trmtWedStart", "trmtWedEnd", element);
                String treatmentThu = XmlUtils.getTreatmentTimeFromElement("trmtThuStart", "trmtThuEnd", element);
                String treatmentFri = XmlUtils.getTreatmentTimeFromElement("trmtFriStart", "trmtFriEnd", element);
                String treatmentSat = XmlUtils.getTreatmentTimeFromElement("trmtSatStart", "trmtSatEnd", element);
                String treatmentSun = XmlUtils.getTreatmentTimeFromElement("trmtSunStart", "trmtSunEnd", element);
                String receiveWeek = XmlUtils.getStringFromElement("rcvWeek", element);
                String receiveSat = XmlUtils.getStringFromElement("rcvSat", element);
                String lunchWeek = XmlUtils.getStringFromElement("lunchWeek", element);
                changeTimeFormat(lunchWeek);
                String lunchSat = XmlUtils.getStringFromElement("lunchSat", element);
                changeTimeFormat(lunchSat);
                String noTreatmentSun = XmlUtils.getStringFromElement("noTrmtSun", element);
                noTreatmentSun = checkNoTreatment(noTreatmentSun);

                String noTreatmentHoliday = XmlUtils.getStringFromElement("noTrmtHoli", element);
                noTreatmentHoliday = checkNoTreatment(noTreatmentHoliday);

                String emergencyDay = XmlUtils.getStringFromElement("emyDayYn", element);
                String emergencyNight = XmlUtils.getStringFromElement("emyNgtYn", element);
                FacilityDetailDto facilityDetailDto = FacilityDetailDto.of(
                        parking, parkingEtc, treatmentMon, treatmentTue, treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun,
                        receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday, emergencyDay, emergencyNight
                );
                System.out.println(facilityDetailDto);
            }
        }
    }

    // 정상 진료: 휴진없음, 휴무 관련 : 휴무, 값이 없는 경우: null
    public static String checkNoTreatment(String dateTime) {
        String treatmentY = "휴진없음";
        String treatmentN = "휴진";
        Set<String> yesTreatment = new HashSet<>(List.of("정상근무", "정규진료", "진료", "휴진없음"));
        Set<String> noTreatment = new HashSet<>(
                List.of("전부휴진", "모두휴진", "휴진", "휴무", "전부휴일", "전부휴무", "매주휴진", "종일휴진", "휴뮤", "휴진입니다.")
        );
        if (dateTime == null) {
            return null;
        }

        String withoutSpace = dateTime.replaceAll(" ", "");
        if (yesTreatment.contains(withoutSpace)) {
            return treatmentY;
        }
        if (noTreatment.contains(withoutSpace)) {
            return treatmentN;
        }
        return dateTime;
    }

    public static String changeTimeFormat(String lunchTime) {
        String check1 = "오후1시~2시";
        String check2 = "12:00 - 13:30";
        String check3 = "1-15시";
        String check4 = "오후 12시30분부터 2시까지";
        String check5 = "오후01시부터";
        String check6 = "09:08부터접수";

        if (lunchTime == null) {
            return null;
        }
        Set<String> noLunch = new HashSet<>(List.of("공란", "휴진", "없음", "휴무", "전체휴진", "오전진료", "무", "점심시간없음"));
        Set<String> needContainCheck = new HashSet<>(List.of("점심시간따로없"));
        Pattern timePattern1 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})시(\\d{2}분?)?([-~])(오후|저녁)?(\\d{1,2})시(\\d{2}분?)?");      //13시(00)(분)-14시(00)(분)
        Pattern timePattern2 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})([:;])(\\d{2})([-~])(오후|저녁)?(\\d{1,2})([:;])(\\d{2})");     //13(:|;)00(-|~)14(:|;)00, 1:00~2:00
        Pattern timePattern3 = Pattern.compile("(\\d{1,2})시?([-~])(\\d{1,2})시?");                           //ex) 13(시)-14(시), 01시-02시 등
        Pattern timePattern4 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})시(\\d{2}분)?부터(오후|저녁)?(\\d{1,2})시(\\d{2}분)?(까지)?");         //1시부터 2시(30분)까지
        Pattern timePattern5 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})시(\\d{2}분)?부터(접수)?");         //09시30분부터
        Pattern timePattern6 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})[:;](\\d{2})부터(접수)?");         //09:30부터

        check1 = check1.replaceAll(" ", "");        //띄어쓰기 제거
        Matcher matcher = timePattern1.matcher(check1);
        if (matcher.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTime = matcher.group(2);
            String startMinute = matcher.group(3);
            String endTime = matcher.group(6);
            String endMinute = matcher.group(7);

            if (startTime.length() == 2 && endTime.length() == 2) { // 5시를 기준으로 판단 (09시는 오전, 01시는 오후)
                if (startTime.charAt(0) == '0') {
                    if (Integer.parseInt(startTime) > 5) {
                        resultStr.append(startTime);
                    } else {
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                } else {
                    resultStr.append(startTime);
                }

                if (startMinute != null) {           // 첫번째 minute
                    resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
                } else {
                    resultStr.append(":00 - ");
                }

                if (endTime.charAt(0) == '0') {     // 두번째 hour
                    resultStr.append((Integer.parseInt(endTime) + 12));
                } else {
                    resultStr.append(endTime);
                }

                if (endMinute != null) {            // 두번째 minute
                    resultStr.append(":").append(endMinute.replace("분", ""));
                } else {
                    resultStr.append(":00");
                }

//            return resultStr;
            } else if (startTime.length() == 1 && endTime.length() == 2) {
                if (Integer.parseInt(startTime) > 5) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {
                    resultStr.append((Integer.parseInt(startTime) + 12));
                }

                if (startMinute != null) {
                    resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
                } else {
                    resultStr.append(":00 - ");
                }

                if (endTime.charAt(0) == '0') {
                    resultStr.append((Integer.parseInt(endTime) + 12));
                } else {
                    resultStr.append(endTime);
                }

                if (endMinute != null) {
                    resultStr.append(":").append(endMinute.replace("분", ""));
                } else {
                    resultStr.append(":00");
                }
            } else if (startTime.length() == 2 && endTime.length() == 1) {
                if (startTime.charAt(0) == '0') {
                    if (Integer.parseInt(startTime) > 5) {
                        resultStr.append(startTime);
                    } else {
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                } else {
                    resultStr.append(startTime);
                }

                if (startMinute != null) {
                    resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
                } else {
                    resultStr.append(":00 - ");
                }

                resultStr.append((Integer.parseInt(endTime) + 12));

                if (endMinute != null) {
                    resultStr.append(":").append(endMinute.replace("분", ""));
                } else {
                    resultStr.append(":00");
                }
                System.out.println(resultStr);
            } else if (startTime.length() == 1 && endTime.length() == 1) {       //오전9시~오후5시30분
                if (Integer.parseInt(startTime) > 5) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {
                    resultStr.append((Integer.parseInt(startTime) + 12));
                }

                if (startMinute != null) {
                    resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
                } else {
                    resultStr.append(":00 - ");
                }

                resultStr.append((Integer.parseInt(endTime) + 12));
                if (endMinute != null) {
                    resultStr.append(":").append(endMinute.replace("분", ""));
                } else {
                    resultStr.append(":00");
                }

            }


            System.out.println("timePattern1 : " + resultStr.toString());
        }

        check2 = check2.replaceAll(" ", "");
        Matcher matcher2 = timePattern2.matcher(check2);
        if (matcher2.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTime = matcher2.group(2);
            String startMinute = matcher2.group(4);
            String endTime = matcher2.group(7);
            String endMinute = matcher2.group(9);

            if (startTime.length() == 1 && endTime.length() == 2) {
                if (Integer.parseInt(startTime) > 5) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {
                    resultStr.append((Integer.parseInt(startTime) + 12));
                }
                resultStr.append(":").append(startMinute).append(" - ");
                if (endTime.charAt(0) == '0') {
                    resultStr.append(Integer.parseInt(endTime) + 12);
                } else {
                    resultStr.append(endTime);
                }
                resultStr.append(":").append(endMinute);
            } else if (startTime.length() == 1 && endTime.length() == 1) {
                if (Integer.parseInt(startTime) > 5) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {
                    resultStr.append((Integer.parseInt(startTime) + 12));
                }
                resultStr.append(":").append(startMinute).append(" - ");
                resultStr.append(String.format("%02d", Integer.parseInt(endTime) + 12));
                resultStr.append(":").append(endMinute);
            } else if (startTime.length() == 2 && endTime.length() == 1) {        // 01:00~1:20
                if (startTime.charAt(0) == '0') {
                    if (Integer.parseInt(startTime) > 5) {
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                } else {
                    resultStr.append(startTime);
                }
                resultStr.append(":").append(startMinute).append(" - ");
                resultStr.append(String.format("%02d", Integer.parseInt(endTime) + 12));
                resultStr.append(":").append(endMinute);
            } else if (startTime.length() == 2 && endTime.length() == 2) {
                if (startTime.charAt(0) == '0') {
                    if (Integer.parseInt(startTime) > 5) {
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                } else {
                    resultStr.append(startTime);
                }
                resultStr.append(":").append(startMinute).append(" - ");
                if (endTime.charAt(0) == '0') {
                    resultStr.append(Integer.parseInt(endTime) + 12);
                } else {
                    resultStr.append(endTime);
                }
                resultStr.append(":").append(endMinute);
            }


            System.out.println("timePattern2 : " + resultStr.toString());
//            return resultStr;
        }

        check3 = check3.replaceAll(" ", "");
        Matcher matcher3 = timePattern3.matcher(check3);
        if (matcher3.find()) {
            String startTime = matcher3.group(1);
            String endTime = matcher3.group(3);
            String resultStr = "";

            if (startTime.length() == 1 && endTime.length() == 1) {  // ex) 2시-3시
                int startTimeInt = Integer.parseInt(startTime);
                if (startTimeInt > 5) {
                    startTime = String.format("%02d:00", startTimeInt);
                } else {
                    startTime = String.format("%02d:00", startTimeInt + 12);
                }
                resultStr = startTime + " - " + String.format("%02d:00", Integer.parseInt(endTime) + 12);
            } else if (startTime.length() == 1 && endTime.length() == 2) {    //ex) 1시-14시
                int startTimeInt = Integer.parseInt(startTime);
                if (startTimeInt > 5) {
                    startTime = String.format("%02d:00", startTimeInt);
                } else {
                    startTime = String.format("%02d:00", startTimeInt + 12);
                }
                resultStr = startTime + " - " + String.format("%02d:00", Integer.parseInt(endTime));
            } else if (startTime.length() == 2 && endTime.length() == 1) {
                resultStr += startTime + ":00" + " - " + String.format("%02d:00", Integer.parseInt(endTime) + 12);
            } else if (startTime.length() == 2 && endTime.length() == 2) {
                resultStr += startTime + ":00" + " - " + endTime + ":00";
            }

            System.out.println("timePattern3 : " + resultStr);
//            return resultStr;
        }

        check4 = check4.replaceAll(" ", "");
        Matcher matcher4 = timePattern4.matcher(check4);
        if (matcher4.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher4.group(1);
            String startTime = matcher4.group(2);
            String startMinute = matcher4.group(3);
            String endTime = matcher4.group(5);
            String endMinute = matcher4.group(6);

            if (startTimeAmPm != null) {
                if (startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {
                    if (startTime.length() == 2) {
                        if (startTime.charAt(0) == '0') {        //ex) 오후 01시부터 02시까지
                            resultStr.append(String.format("%02d", (Integer.parseInt(startTime) + 12)));
                        } else {        //ex) 오후 13시부터 14시까지
                            resultStr.append(startTime);
                        }
                    } else {
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime) + 12));
                    }
                }
                if (startMinute != null) {
                    resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
                } else {
                    resultStr.append(":00").append(" - ");
                }
                if (endTime.length() == 2) {
                    if (endTime.charAt(0) == '0') {
                        resultStr.append(Integer.parseInt(endTime) + 12);
                    } else {
                        resultStr.append(endTime);
                    }
                } else {
                    resultStr.append(Integer.parseInt(endTime) + 12);
                }
                if (endMinute != null) {
                    resultStr.append(":").append(endMinute.replace("분", ""));
                } else {
                    resultStr.append(":00");
                }
            } else {    // 처음 부분에 오전/오후가 없을시 1시부터 오후14시30분
                if (startTime.length() == 2) {
                    // 시간
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {
                            resultStr.append(startTime);
                        } else {
                            resultStr.append((Integer.parseInt(startTime) + 12));
                        }
                    } else {
                        resultStr.append(startTime);
                    }
                    // 분
                    if (startMinute != null) {
                        resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
                    } else {
                        resultStr.append(":00").append(" - ");
                    }
                    // 시간
                    if (endTime.length() == 2) {
                        if (endTime.charAt(0) == '0') {
                            resultStr.append(Integer.parseInt(endTime) + 12);
                        } else {
                            resultStr.append(endTime);
                        }
                    } else {
                        resultStr.append(Integer.parseInt(endTime) + 12);
                    }
                    // 분
                    if (endMinute != null) {
                        resultStr.append(":").append(endMinute.replace("분", ""));
                    } else {
                        resultStr.append(":00");
                    }

                } else {    //ex) 1시20분부터
                    //시간
                    if (Integer.parseInt(startTime) > 5) {
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                    // 분
                    if (startMinute != null) {
                        resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
                    } else {
                        resultStr.append(":00").append(" - ");
                    }
                    // 시간
                    if (endTime.length() == 2) {
                        if (endTime.charAt(0) == '0') {
                            resultStr.append(Integer.parseInt(endTime) + 12);
                        } else {
                            resultStr.append(endTime);
                        }
                    } else {
                        resultStr.append(Integer.parseInt(endTime) + 12);
                    }
                    // 분
                    if (endMinute != null) {
                        resultStr.append(":").append(endMinute.replace("분", ""));
                    } else {
                        resultStr.append(":00");
                    }
                }
            }

            System.out.println("timePattern4 : " + resultStr);
        }

        check5 = check5.replaceAll(" ", "");
        Matcher matcher5 = timePattern5.matcher(check5);
        if (matcher5.find()) {        //(오전|오후)?(\d{1,2})시(\d{2}분)?부터(접수)?
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher5.group(1);
            String startTime = matcher5.group(2);
            String startMinute = matcher5.group(3);

            if (startTimeAmPm != null) {
                if (startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {    //오후
                    if (startTime.length() == 2) {
                        if (startTime.charAt(0) == '0') {
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        } else {
                            resultStr.append(startTime);
                        }
                    } else {
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            } else {
                if (startTime.length() == 2) {
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {   //ex) 09시부터 -> 09:00 부터
                            resultStr.append(startTime);
                        } else {                        //ex) 01시30분부터 -> 13:30 부터
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        }
                    } else {                            //ex) 15시30분부터 -> 15:30 부터
                        resultStr.append(startTime);
                    }
                } else {
                    if (Integer.parseInt(startTime) > 5) {
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            }
            // 분
            if (startMinute != null) {
                resultStr.append(":").append(startMinute.replace("분", "")).append(" 부터");
            } else {
                resultStr.append(":00").append(" 부터");
            }

            System.out.println("timePattern5 : " + resultStr);
        }

        check6 = check6.replaceAll(" ", "");       // (오전|오후|아침)?(\d{1,2})[:;](\d{2}분)부터(접수)?
        Matcher matcher6 = timePattern6.matcher(check6);
        if (matcher6.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher6.group(1);
            String startTime = matcher6.group(2);
            String startMinute = matcher6.group(3);

            if (startTimeAmPm != null) {
                if (startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {    //오후
                    if (startTime.length() == 2) {
                        if (startTime.charAt(0) == '0') {
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        } else {
                            resultStr.append(startTime);
                        }
                    } else {
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            } else {
                if (startTime.length() == 2) {
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {   // ex) 09:00부터
                            resultStr.append(startTime);
                        } else {                                // ex) 01:00부터
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        }
                    } else {                                    // ex) 11:00부터
                        resultStr.append(startTime);
                    }
                } else {
                    if (Integer.parseInt(startTime) > 5) {       // ex) 9:00부터
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {                                    // ex) 1:00부터
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            }
            resultStr.append(":").append(startMinute).append(" 부터");

            System.out.println("timePattern6 : " + resultStr);
        }

        return null;
    }
}

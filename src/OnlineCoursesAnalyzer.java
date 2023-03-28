import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath,
                    StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",
                        -1);
                Course course = new Course(info[0], info[1], new Date(info[2]),
                        info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]),
                        Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]),
                        Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]),
                        Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]),
                        Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> map = courses.stream().collect(Collectors.groupingBy(Course::getInstitution,
                Collectors.summingInt(Course::getParticipants)));
        // 将map中的entry存放到List中
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        // 使用Collections.sort方法对List进行排序，自定义Comparator按key中字母顺序排序
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        // 创建新的有序Map
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> map = courses.stream().collect(Collectors.groupingBy(Course::getInstitutionAndSubject,
                Collectors.summingInt(Course::getParticipants)));
        // 将map中的entry存放到List中
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        // 使用Collections.sort方法对List进行排序，自定义Comparator按key中字母顺序排序
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int diff = -(o1.getValue().compareTo(o2.getValue()));
                return diff != 0 ? diff : o1.getKey().compareTo(o2.getKey());
            }
        });
        // 创建新的有序Map
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<Map<String, Integer>>> result = new HashMap<>();
        List<Course> in_list = new ArrayList<>();
        List<Course> co_list = new ArrayList<>();
        for (Course cours : courses) {
            String string = cours.instructors;
            if (string.contains(",")) {
                co_list.add(cours);
            } else {
                in_list.add(cours);
            }
            String[] strings = string.split(", ");
            for (String s : strings) {
                List<Map<String, Integer>> list = new ArrayList<>();
                Map<String, Integer> in_map = new HashMap<>();
                Map<String, Integer> co_map = new HashMap<>();
                list.add(in_map);
                list.add(co_map);
                result.put(s, list);
            }
        }
        Map<String, List<List<String>>> map = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Integer>>> entry : result.entrySet()) {
            String mapKey = entry.getKey();
            List<Map<String, Integer>> mapValue = entry.getValue();
            List<List<String>> lists = new ArrayList<>();
            for (Course course : in_list) {
                if (course.instructors.contains(mapKey)) {
                    mapValue.get(0).put(course.title, 1);
                }
            }
            for (Course course : co_list) {
                String[] strings = course.instructors.split(", ");
                for (String name: strings) {
                    if (name.equals(mapKey)) {
                        mapValue.get(1).put(course.title, 1);
                    }
                }

            }
            lists.add(new ArrayList<>(mapValue.get(0).keySet()));
            lists.add(new ArrayList<>(mapValue.get(1).keySet()));
            Collections.sort(lists.get(0), Comparator.naturalOrder());
            Collections.sort(lists.get(1), Comparator.naturalOrder());
            map.put(mapKey, lists);
        }
        return map;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        if (by.equals("hours")) {
            Collections.sort(courses, (o1, o2) -> {
                int diff = -Double.compare(o1.totalHours, o2.getTotalHours());
                return diff != 0 ? diff : o1.title.compareTo(o2.getTitle());
            });
        }
        if (by.equals("participants")) {
            Collections.sort(courses, (o1, o2) -> {
                int diff = -Integer.compare(o1.participants, o2.participants);
                return diff != 0 ? diff : o1.title.compareTo(o2.getTitle());
            });
        }
        List<String> result = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < topK; i++) {
            Course course = courses.get(j);
            j++;
            String name = course.title;
            if (!result.contains(name)) {
                result.add(name); }
            else{
                i--;
            }
        }
        return result;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        courseSubject = courseSubject.toLowerCase();
        String finalCourseSubject = courseSubject;
        Predicate<Course> predicate = course -> {
            boolean flag = false;
            String subject = course.subject.toLowerCase();
            if (subject.contains(finalCourseSubject)){
                flag = true;
            }
            return flag;
        };
        Predicate<Course> predicate1 = course -> course.percentAudited >= percentAudited;
        Predicate<Course> predicate2 = course -> course.totalHours <= totalCourseHours;
        List<Course> courseList = courses.stream().filter(predicate).filter(predicate1).filter(predicate2).sorted(new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return o1.title.compareTo(o2.title);
            }
        }).toList();
        List<String> result = new ArrayList<>();
        for (Course course: courseList){
            if (!result.contains(course.title)){
                result.add(course.title);
            }
        }
        return result;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        // calculate the average values for each course
        List<CourseStats> summaries = courses.stream()
                .collect(Collectors.groupingBy(Course::getNumber))
                .entrySet().stream()
                .map(e -> new CourseStats(e.getKey(),e.getValue(),age,gender,isBachelorOrHigher)).toList();

        // calculate similarity values for each course
        List<CourseStats> sortedSummaries = summaries.stream()
                .sorted(Comparator.comparing(CourseStats::getSimilarity))
                        .limit(10).toList();

        List<String> topCourses = sortedSummaries.stream()
                .map(CourseStats::getTitle).toList();
//        Map<String,List<Course>> map = courses.stream().collect(Collectors.groupingBy(Course::getNumber));
//        ArrayList<Double> doubles = new ArrayList<>();
//        for (Map.Entry<String, List<Course>> entry : map.entrySet()) {
//            double avgAge = 0;
//            double avgMale = 0;
//            double avgDegree = 0;
//            List<Course> list = entry.getValue();
//            for (Course course:list){
//                avgAge+=course.medianAge;
//                avgMale+=course.percentMale;
//                avgDegree+=course.percentDegree;
//            }
//            avgAge = avgAge/list.size();
//            avgDegree = avgDegree/list.size();
//            avgMale =avgMale/list.size();
//            double similar = Math.pow((age-avgAge),2)+Math.pow((gender*100-avgMale),2)+
//                    Math.pow((isBachelorOrHigher*100-avgDegree),2);
//            doubles.add(similar);
//            for (Course course:list){
//                course.setSimilarity(similar);
//            }
//        }
//        Collections.sort(doubles);
//        System.out.println(doubles);
//        Collections.sort(courses, Comparator.comparingDouble(o -> o.similarity));

        // Calculate the average Median Age, average % Male, and average % Bachelor's Degree or Higher for each course
        List<String> courseAverages = courses.stream()
                .collect(Collectors.groupingBy(Course::getNumber, Collectors.toList()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    double averageMedianAge = e.getValue()
                            .stream()
                            .mapToDouble(Course::getMedianAge)
                            .average()
                            .orElse(0);
                    double averageMale = e.getValue()
                            .stream()
                            .mapToDouble(Course::getPercentMale)
                            .average()
                            .orElse(0);
                    double averageBachelorOrHigher = e.getValue()
                            .stream()
                            .mapToDouble(Course::getPercentDegree)
                            .average()
                            .orElse(0);
                    return Arrays.asList(averageMedianAge, averageMale, averageBachelorOrHigher);
                }))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Math.pow(age - e.getValue().get(0), 2)
                        + Math.pow(gender * 100 - e.getValue().get(1), 2)
                        + Math.pow(isBachelorOrHigher * 100 - e.getValue().get(2), 2)))
                .entrySet()
                .stream()

                .map( entry -> new AbstractMap.SimpleEntry<>(Objects.requireNonNull(courses.stream()
                        .filter(c -> c.getNumber().equals(entry.getKey()))
                        .filter(c -> c.getLaunchDate() != null)
                        .max(Comparator.comparing(Course::getLaunchDate))
                        .orElse(null))
                        .title
                        ,
                        entry.getValue()))
                .sorted(Comparator.<Map.Entry<String, Double>>comparingDouble(Map.Entry::getValue)
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        return courseAverages;
    }

}

class CourseStats {
    private String courseNumber;
    private String title;
    private double medianAge;
    private double percentMale;
    private double percentBachelorOrHigher;
    private double similarity;

    private List<Course> courses;

    public CourseStats(String courseNumber, List<Course> courses, int age, int gender, int isBachelorOrHigher) {
        this.courses = courses;
        this.courseNumber = courseNumber;
        this.title = courses.stream().max(Comparator.comparing(Course::getLaunchDate)).orElseThrow().getTitle();
        this.medianAge = courses.stream().mapToDouble(Course::getMedianAge).average().orElse(0);
        this.percentMale = courses.stream().mapToDouble(Course::getPercentMale).average().orElse(0);
        this.percentBachelorOrHigher = courses.stream().mapToDouble(Course::getPercentDegree).average().orElse(0);
        this.similarity = Math.pow(age - medianAge, 2)
                + Math.pow(gender * 100 - percentMale, 2)
                + Math.pow(isBachelorOrHigher * 100 - percentBachelorOrHigher, 2);
    }
    public String getTitle() {
        return title;
    }

    public double getSimilarity() {
        return similarity;
    }
}


class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    double similarity;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    // 1
    public String getInstitution() {
        return this.institution;
    }

    public int getParticipants() {
        return this.participants;
    }

    // 2
    public String getInstitutionAndSubject() {
        return this.institution + "-" + this.subject;
    }

    //3
    public String getInstructors() {
        return this.instructors;
    }

    //4
    public double getTotalHours() {
        return this.totalHours;
    }

    public String getTitle() {
        return this.title;
    }

    //6
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double getMedianAge() {
        return this.medianAge;
    }

    public double getPercentMale() {
        return this.percentMale;
    }

    public double getPercentDegree() {
        return this.percentDegree;
    }

    public String getNumber() {
        return this.number;
    }

    public double getSimilarity() {
        return this.similarity;
    }
    public Date getLaunchDate(){
        return this.launchDate;
    }
}


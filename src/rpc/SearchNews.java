package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import api.NYTimesAPI;
import db.DBConnection;

/**
 * Servlet implementation class SearchNews
 */
@WebServlet("/news")
public class SearchNews extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchNews() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {

			// Parse input parameters from client.
			JSONObject input = RpcParser.parseInput(request);

			if (input.has("user_id") && input.has("keyword")) {
				String keyword = input.getString("keyword");
				String userId = input.getString("user_id");
				System.out
						.println("SearchNews gets a POST request with keyword = "
								+ keyword + ", userId = " + userId);

				JSONArray jsonArray = NYTimesAPI.searchNews(keyword);
				
				List<News> allNews = new ArrayList<>();

				DBConnection connection = new DBConnection();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					News news = new News(jsonObject);
					connection.insertNews(news);
					allNews.add(news);
				}
				
				RpcParser.parseOutput(response, new JSONArray(allNews));
			} else {
				System.err
						.println("SearchNews gets an invalid POST request that "
								+ "does not contain keyword or user_id. Return null.");
			}
		} catch (JSONException e) {
			System.out.println("JSON format is wrong");
			e.printStackTrace();
		}
	}

}
